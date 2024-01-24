package bsu.rpact.medionefrontend.utils;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import org.vaadin.addons.badge.Badge;

public class FhirBadgeInterpretator {

    public static Badge interpret(String code, String display){
        Badge badge = null;
        switch (code){
            case "H":{
                badge = buildBadge(display, Badge.BadgeVariant.ERROR,true,true, VaadinIcon.ARROW_UP.create());
                break;
            }
            case "L":{
                badge = buildBadge(display, Badge.BadgeVariant.ERROR,true,true, VaadinIcon.ARROW_DOWN.create());
                break;
            }
            case "N": {
                badge = buildBadge(display, Badge.BadgeVariant.NORMAL,true,true, VaadinIcon.CHECK.create());
                break;
            }
            default: badge=getDefault(display);
        }
        return badge;
    }

    private static Badge buildBadge(String code, Badge.BadgeVariant variant, boolean primary, boolean pill, Icon icon){
        Badge badge = new Badge(code);
        badge.setVariant(variant);
        badge.setPrimary(primary);
        badge.setPill(pill);
        badge.setIcon(icon);
        return badge;
    }

    private static Badge getDefault(String code){
        Badge badge = new Badge(code);
        badge.setVariant(Badge.BadgeVariant.NORMAL);
        badge.setPrimary(true);
        badge.setPill(true);
        return badge;
    }

}
