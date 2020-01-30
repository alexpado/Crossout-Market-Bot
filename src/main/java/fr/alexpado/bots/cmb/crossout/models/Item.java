package fr.alexpado.bots.cmb.crossout.models;

import fr.alexpado.bots.cmb.discord.DiscordBot;
import fr.alexpado.bots.cmb.interfaces.JSONModel;
import fr.alexpado.bots.cmb.tools.Utilities;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Getter
public class Item extends JSONModel {

    private int id;
    private String name;
    private String description;
    private boolean removed;
    private boolean craftable;
    private int sellPrice;
    private int buyPrice;
    private int craftingSellSum;
    private int craftingBuySum;
    private long lastUpdate;
    private Category category;
    private Faction faction;
    private Rarity rarity;
    private Type type;

    public Item(JSONObject dataSource) throws Exception {
        super(dataSource);
    }

    public static Optional<Item> from(JSONObject dataSource) {
        try {
            return Optional.of(new Item(dataSource));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public boolean reload(JSONObject dataSource) {
        try {
            this.id = dataSource.getInt("id");
            this.name = dataSource.getString("name");

            if (dataSource.get("description") == JSONObject.NULL) {
                this.description = "";
            } else {
                this.description = Utilities.removeHTML(dataSource.getString("description"));
            }

            this.rarity = new Rarity(dataSource.getInt("rarityId"), dataSource.getString("rarityName"));
            this.sellPrice = dataSource.getInt("sellPrice");
            this.buyPrice = dataSource.getInt("buyPrice");
            this.craftingSellSum = dataSource.getInt("craftingSellSum");
            this.craftingBuySum = dataSource.getInt("craftingBuySum");
            this.removed = dataSource.getInt("removed") == 1;
            this.craftable = !(craftingSellSum == 0 || craftingBuySum == 0);

            this.lastUpdate = Instant.parse(dataSource.getString("timestamp") + ".000Z").toEpochMilli() / 1000;
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getThumbnailUrl() {
        Date today = Calendar.getInstance().getTime();
        String thumbnailUrl = "https://crossoutdb.com/img/items/%s.png?d=%tY%tm%td";
        return String.format(thumbnailUrl, this.getId(), today, today, today);
    }

    private String getWebUrl() {
        String webUrl = "https://crossoutdb.com/item/%s?ref=crossoutmarketbot";
        return String.format(webUrl, this.getId());
    }

    public EmbedBuilder getDiffEmbed(JDA jda, int sellPrice, int buyPrice) {
        EmbedBuilder builder = this.getRawEmbed(jda);

        if (!this.removed) {
            String currentSellPrice = Utilities.money(this.sellPrice, "Coins");
            String currentBuyPrice = Utilities.money(this.buyPrice, "Coins");

            String diffSellPrice = Utilities.money(this.sellPrice - sellPrice, "");
            String diffBuyPrice = Utilities.money(this.buyPrice - buyPrice, "");

            builder.addField("Buy it for", String.format("%s ( %s )", currentSellPrice, diffSellPrice), true);
            builder.addField("Sell it for", String.format("%s ( %s )", currentBuyPrice, diffBuyPrice), true);
        }

        return builder;
    }

    public EmbedBuilder getAsEmbed(JDA jda) {
        EmbedBuilder builder = this.getRawEmbed(jda);

        if (!this.removed) {
            builder.addField("Buy it for", Utilities.money(this.sellPrice, "Coins"), true);
            builder.addField("Sell it for", Utilities.money(this.buyPrice, "Coins"), true);

            if (this.craftable) {
                builder.addField("Buy Craft items for", Utilities.money(this.craftingSellSum, "Coins"), true);
                builder.addField("Sell Craft items for", Utilities.money(this.craftingBuySum, "Coins"), true);
            }
        }

        return builder;
    }

    private EmbedBuilder getRawEmbed(JDA jda) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setAuthor("Click here to invite the bot.", DiscordBot.INVITE, jda.getSelfUser().getAvatarUrl());
        builder.setTitle(this.name, String.format("https://crossoutdb.com/item/%s?ref=crossoutmarketbot", this.id));
        builder.setDescription(this.description);

        builder.setThumbnail(String.format("https://crossoutdb.com/img/items/%s.png?ref=crossoutmarketbot", this.id));
        builder.setImage(String.format("http://bots.alexpado.fr:8181/chart/%s/%s/chart.png", this.id, this.lastUpdate));

        if (this.removed) {
            builder.addField("Removed", "This item is no longer available.", true);
        }

        if (this.rarity == null) {
            builder.setColor(Color.BLACK);
        } else {
            builder.setColor(this.rarity.getColor());
        }

        return builder;
    }

    @Override
    public String toString() {
        return name;
    }

}