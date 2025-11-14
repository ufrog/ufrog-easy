package net.ufrog.easy.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.authorizes.Authorize;
import net.ufrog.easy.authorizes.AuthorizeFilter;
import net.ufrog.easy.authorizes.JWTAuthorize;
import net.ufrog.easy.caches.CacheUtil;
import net.ufrog.easy.configurations.properties.*;
import net.ufrog.easy.contracts.QueryRequestArgumentResolver;
import net.ufrog.easy.exceptions.CommonException;
import net.ufrog.easy.exceptions.InvalidPropertyException;
import net.ufrog.easy.filters.CorsFilter;
import net.ufrog.easy.i18n.EasyMessageSource;
import net.ufrog.easy.i18n.I18NChangeCallback;
import net.ufrog.easy.i18n.SpringMessageSource;
import net.ufrog.easy.interceptors.ApplicationInterceptor;
import net.ufrog.easy.interceptors.AuthorizeInterceptor;
import net.ufrog.easy.interceptors.PropertiesLoadInterceptor;
import net.ufrog.easy.json.LongToStringSerializer;
import net.ufrog.easy.log.RequestLogAspect;
import net.ufrog.easy.log.RequestLogProcessor;
import net.ufrog.easy.utils.ObjectUtil;
import net.ufrog.easy.utils.StringUtil;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 自动配置
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Slf4j
@Configuration
@AutoConfigureBefore(name = {"org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration"})
@EnableConfigurationProperties({CacheProperties.class, EasyProperties.class, FilterProperties.class,
        I18NProperties.class, InterceptorProperties.class, RequestLogProperties.class})
public class EasyAutoConfiguration implements WebMvcConfigurer {

    /** Springframework application context */
    private final ApplicationContext applicationContext;

    /** 缓存参数 */
    private final CacheProperties cacheProperties;

    /** 基础参数 */
    private final EasyProperties easyProperties;

    /** 过滤器参数 */
    private final FilterProperties filterProperties;

    /** 国际化参数 */
    private final I18NProperties i18nProperties;

    /** 拦截器参数 */
    private final InterceptorProperties interceptorProperties;

    /** 请求日志参数 */
    private final RequestLogProperties requestLogProperties;

    /**
     * 构造函数
     *
     * @param applicationContext Springframework application context
     * @param cacheProperties Cache properties
     * @param easyProperties Easy properties
     * @param filterProperties Filter properties
     * @param i18nProperties I18N properties
     * @param interceptorProperties Interceptor properties
     * @param requestLogProperties Request log properties
     */
    public EasyAutoConfiguration(ApplicationContext applicationContext,
                                 CacheProperties cacheProperties,
                                 EasyProperties easyProperties,
                                 FilterProperties filterProperties,
                                 I18NProperties i18nProperties,
                                 InterceptorProperties interceptorProperties,
                                 RequestLogProperties requestLogProperties) {
        this.applicationContext = applicationContext;
        this.cacheProperties = cacheProperties;
        this.easyProperties = easyProperties;
        this.filterProperties = filterProperties;
        this.i18nProperties = i18nProperties;
        this.interceptorProperties = interceptorProperties;
        this.requestLogProperties = requestLogProperties;
    }

    @PostConstruct
    private void init() {
        net.ufrog.easy.ApplicationContext.init(applicationContext);
        CacheUtil.init(cacheProperties);
    }

    @Override
    public void addInterceptors(@Nonnull InterceptorRegistry registry) {
        addApplicationInterceptor(registry);
        addAuthorizeInterceptor(registry);
        addPropertiesLoadInterceptor(registry);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.stream().filter(c -> c instanceof MappingJackson2HttpMessageConverter).forEach(c -> {
            MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = (MappingJackson2HttpMessageConverter) c;
            ObjectMapper objectMapper = mappingJackson2HttpMessageConverter.getObjectMapper();

            SimpleModule simpleModule = new SimpleModule();
            simpleModule.addSerializer(Long.TYPE, LongToStringSerializer.instance);
            simpleModule.addSerializer(Long.class, LongToStringSerializer.instance);
            objectMapper.registerModule(simpleModule);
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        });
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new QueryRequestArgumentResolver());
    }

    @Bean
    @ConditionalOnMissingBean(value = EasyMessageSource.class)
    public EasyMessageSource easyMessageSource() {
        return new SpringMessageSource(i18nProperties);
    }

    @Bean
    @ConditionalOnMissingBean(value = I18NChangeCallback.class)
    public I18NChangeCallback i18NChangeCallback() {
        return new I18NChangeCallback.EmptyChangeCallback();
    }

    @Bean
    @ConditionalOnProperty(prefix = "easy.interceptors.authorize", name = "enabled", havingValue = "true")
    public Authorize authorize() {
        String type = interceptorProperties.getAuthorize().getType().toLowerCase();
        if (StringUtil.equals("jwt", type)) {
            return new JWTAuthorize(easyProperties.isProduction(), interceptorProperties.getAuthorize().getIgnoreUris(), getFilters(), easyProperties.getSecret());
        }
        throw new InvalidPropertyException("easy.interceptors.authorize", "type", type);
    }

    @Bean
    public WebServerFactoryCustomizer<UndertowServletWebServerFactory> webServerFactoryWebServerFactoryCustomizer() {
        return factory -> factory.addDeploymentInfoCustomizers(deploymentInfo -> {
            WebSocketDeploymentInfo webSocketDeploymentInfo = new WebSocketDeploymentInfo();
            webSocketDeploymentInfo.setBuffers(new DefaultByteBufferPool(false, 1024));
            deploymentInfo.addServletContextAttribute("io.undertow.websockets.jsr.WebSocketDeploymentInfo", webSocketDeploymentInfo);
        });
    }

    @Bean
    @ConditionalOnProperty(prefix = "easy.filter.cors", name = "enabled", havingValue = "true")
    public FilterRegistrationBean<CorsFilter> corsFilterRegistrationBean() {
        String allowedOriginProvider = filterProperties.getCors().getAllowedOriginProvider();
        List<String> allowedOrigins = filterProperties.getCors().getAllowedOrigins();
        List<String> allowedMethods = filterProperties.getCors().getAllowedMethods();
        long maxAge = filterProperties.getCors().getMaxAge();
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>();
        CorsFilter.AllowOriginProvider allowOriginProvider = StringUtil.isEmpty(allowedOriginProvider) ?
                new CorsFilter.SimpleAllowOriginProvider(allowedOrigins) :
                net.ufrog.easy.ApplicationContext.getBean(allowedOriginProvider, CorsFilter.AllowOriginProvider.class);

        bean.setFilter(new CorsFilter(allowOriginProvider, allowedMethods, maxAge));
        bean.addUrlPatterns("/*");
        bean.setName("corsFilter");
        bean.setOrder(1);
        log.info("Register CORS-filter successful.");
        return bean;
    }

    @Bean
    @ConditionalOnProperty(prefix = "easy.request-log", name = "enabled", havingValue = "true")
    public RequestLogAspect requestLogAspect() {
        RequestLogProcessor requestLogProcessor = ObjectUtil.newInstance(requestLogProperties.getProcessor());
        return new RequestLogAspect(requestLogProperties, requestLogProcessor);
    }

    /**
     * 添加应用拦截器
     *
     * @param registry 拦截注册器
     */
    private void addApplicationInterceptor(@Nonnull InterceptorRegistry registry) {
        registry.addInterceptor(new ApplicationInterceptor());
        log.info("Register application-interceptor successful.");
    }

    /**
     * 添加认证拦截器
     *
     * @param registry 拦截注册器
     */
    private void addAuthorizeInterceptor(@Nonnull InterceptorRegistry registry) {
        if (interceptorProperties.getAuthorize().isEnabled()) {
            registry.addInterceptor(new AuthorizeInterceptor(authorize()));
        }
    }

    /**
     * 添加参数加载拦截器
     *
     * @param registry 拦截注册器
     */
    private void addPropertiesLoadInterceptor(@Nonnull InterceptorRegistry registry) {
        if (interceptorProperties.getPropertiesLoad().isEnabled()) {
            PropertiesLoadInterceptor.PropertiesLoader propertiesLoader = null;
            if (!StringUtil.isEmpty(interceptorProperties.getPropertiesLoad().getLoaderBeanName())) {
                propertiesLoader = net.ufrog.easy.ApplicationContext.getBean(interceptorProperties.getPropertiesLoad().getLoaderBeanName(), PropertiesLoadInterceptor.PropertiesLoader.class);
            } else if (interceptorProperties.getPropertiesLoad().getLoaderClass() != null) {
                propertiesLoader = ObjectUtil.newInstance(interceptorProperties.getPropertiesLoad().getLoaderClass());
            }

            // Register properties load interceptor if properties loader exists
            if (propertiesLoader != null) {
                registry.addInterceptor(new PropertiesLoadInterceptor(propertiesLoader));
                log.info("Register properties-load-interceptor successful.");
            } else {
                throw new CommonException("Cannot get properties loader instance.");
            }
        }
    }

    /**
     * 读取认证过滤器
     *
     * @return 认证过滤器
     */
    private AuthorizeFilter[] getFilters() {
        AuthorizeFilter[] filters = null;
        if (interceptorProperties.getAuthorize().getFilters() != null && interceptorProperties.getAuthorize().getFilters().length > 0) {
            filters = new AuthorizeFilter[interceptorProperties.getAuthorize().getFilters().length];
            for (int i = 0; i < interceptorProperties.getAuthorize().getFilters().length; i++) {
                filters[i] = ObjectUtil.newInstance(interceptorProperties.getAuthorize().getFilters()[i]);
            }
        }
        return filters;
    }
}
