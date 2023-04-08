package bsu.rpact.medionefrontend.utils.mapper;

import org.jsoup.select.Elements;

public interface HtmlMapper<T> extends Mapper{
    T map(Elements rows);
}
