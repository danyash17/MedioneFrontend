package bsu.rpact.medionefrontend.security.filter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


public class RequestDecorator extends HttpServletRequestWrapper {

    private Map<String, String> headerMap = new ConcurrentHashMap<>();
    private List<Cookie> cookieList = new CopyOnWriteArrayList<>();

    public RequestDecorator(HttpServletRequest request) {
        super(request);
    }

    public void addHeader(String name, String value) {
        headerMap.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        String headerValue = super.getHeader(name);
        if (headerMap.containsKey(name)) {
            headerValue = headerMap.get(name);
        }
        return headerValue;
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        List<String> names = Collections.list(super.getHeaderNames());
        for (String name : headerMap.keySet()) {
            names.add(name);
        }
        return Collections.enumeration(names);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        List<String> values = Collections.list(super.getHeaders(name));
        if (headerMap.containsKey(name)) {
            values.add(headerMap.get(name));
        }
        return Collections.enumeration(values);
    }

    @Override
    public Cookie[] getCookies() {
        return cookieList.toArray(new Cookie[0]);
    }

    public void deleteCookie(Cookie cookie){
        Optional<Cookie> optional = cookieList.stream().filter(item -> item.equals(cookie)).findAny();
        if(optional.isPresent()) {
            cookieList.remove(optional.get());
        }
    }



}