package fr.alexpado.bots.cmb.models;

import fr.alexpado.bots.cmb.models.keys.TranslationKey;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@Getter
@IdClass(TranslationKey.class)
@Setter
public class Translation {

    public static final String DISCORD_INVITE = "general.bot.invite";

    public static final String GENERAL_ERROR = "general.error";

    public static final String ITEM_LIST = "command.item.list";
    public static final String ITEM_NOT_FOUND = "command.item.notFound";
    public static final String ITEM_REMOVED_LABEL = "general.item.removed.label";
    public static final String ITEM_REMOVED_DESC = "general.item.removed.desc";
    public static final String ITEM_BUY = "general.item.buy";
    public static final String ITEM_SELL = "general.item.sell";
    public static final String ITEM_CRAFT_BUY = "general.item.craft.buy";
    public static final String ITEM_CRAFT_SELL = "general.item.craft.sell";
    public static final String CURRENCY = "general.currency";
    public static final String PACK_NOT_FOUND = "command.pack.notFound";
    public static final String PACK_LIST = "command.pack.list";
    public static final String WATCHER_LIST = "command.watchlist.list";
    public static final String WATCHER_EMPTY = "command.watchlist.empty";
    public static final String WATCHER_TYPE_NORMAL = "general.watchers.normal";
    public static final String WATCHER_TYPE_SELL_UNDER = "general.watchers.sell.under";
    public static final String WATCHER_TYPE_SELL_OVER = "general.watchers.sell.over";
    public static final String WATCHER_TYPE_BUY_UNDER = "general.watchers.buy.under";
    public static final String WATCHER_TYPE_BUY_OVER = "general.watchers.buy.over";
    public static final String WATCHER_TYPE_ADVANCED = "general.watchers.other";

    public static final String WATCHER_WRONG_TYPE = "command.watch.wrong.type";
    public static final String WATCHER_WRONG_VALUE = "command.watch.wrong.value";
    public static final String WATCHER_WRONG_PRICE = "command.watch.wrong.price";
    public static final String WATCHER_WRONG_FOR = "command.watch.wrong.for";
    public static final String WATCHER_MULTIPLE_ITEM = "command.watch.multiple";
    public static final String WATCHER_ADDED = "command.watch.added";

    public static final String WATCHER_NOT_FOUND = "general.watcher.notFound";
    public static final String WATCHER_FORBIDDEN = "general.watcher.forbidden";
    public static final String WATCHER_UPDATED = "command.watch.updated";
    public static final String WATCHER_ITEM_REMOVED = "general.watchers.item.removed";
    public static final String WATCHER_REMOVED = "general.watchers.removed";

    @Id
    @Column(length = 100)
    private String translationKey;

    @Id
    @Column(length = 3)
    private String language;

    private String text;

}
