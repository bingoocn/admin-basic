package com.cngc.admin.security.token;

import com.cngc.admin.security.Principal;
import com.cngc.admin.security.PrincipalDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 用于token中认证用户信息的处理转换.
 *
 * @author duanyl
 */
public class AdminUserAuthenticationConverter implements UserAuthenticationConverter {


    /**
     * 用户id标识符
     */
    private final static String USERID = "user_id";


    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        Map<String, Object> response = new LinkedHashMap<>();
        PrincipalDetails user = (PrincipalDetails) authentication.getPrincipal();
        response.put(USERNAME, authentication.getName());
        response.put(USERID, user.getId());
        if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            response.put(AUTHORITIES, AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
        }
        return response;
    }

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        Object principal = new Principal((String) map.get(USERID), (String) map.get(USERNAME));
        Collection<? extends GrantedAuthority> authorities = getAuthorities(map);
        return new UsernamePasswordAuthenticationToken(principal, "N/A", authorities);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
        Object authorities = map.get(AUTHORITIES);
        if (authorities instanceof String) {
            return AuthorityUtils.commaSeparatedStringToAuthorityList((String) authorities);
        }
        if (authorities instanceof Collection) {
            return AuthorityUtils.commaSeparatedStringToAuthorityList(StringUtils
                    .collectionToCommaDelimitedString((Collection<?>) authorities));
        }
        throw new IllegalArgumentException("Authorities must be either a String or a Collection");
    }
}
