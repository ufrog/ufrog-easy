package net.ufrog.easy.i18n;

import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.ApplicationRequest;

import java.util.Locale;
import java.util.Optional;

/**
 * 会话地区来源实现
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2026-03-08
 * @since 3.5.3
 */
@Slf4j
public class SessionLocaleSource implements EasyLocaleSource {

    private static final String SESSION_LOCALE_KEY = "session.locale";

    @Override
    public Locale getLocale() {
        return ApplicationRequest.getCurrent().map(v -> v.getSession(SESSION_LOCALE_KEY, Locale.class).orElseGet(() -> {
            log.warn("There isn't a session locale available, using default.");
            Locale locale = v.getLocale();
            setLocale(locale);
            return locale;
        })).orElseGet(() -> {
            log.warn("Cannot get current application request, return default locale.");
            return Locale.getDefault();
        });
    }

    @Override
    public void setLocale(Locale locale) {
        Optional<ApplicationRequest> optional = ApplicationRequest.getCurrent();
        if (optional.isPresent()) {
            optional.get().setSession(SESSION_LOCALE_KEY, locale);
        } else {
            log.warn("Cannot get current application and set locale failure.");
        }
    }
}
