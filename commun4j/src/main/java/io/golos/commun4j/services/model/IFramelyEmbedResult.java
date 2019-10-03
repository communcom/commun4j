package io.golos.commun4j.services.model;

import com.squareup.moshi.Json;

import java.util.List;

public class IFramelyEmbedResult {
    private Meta meta;
    private Links links;
    private List<String> rel;
    private String html;

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public List<String> getRel() {
        return rel;
    }

    public void setRel(List<String> rel) {
        this.rel = rel;
    }

    @Override
    public String toString() {
        return "IFramelyEmbedResult{" +
                "meta=" + meta +
                ", links=" + links +
                ", rel=" + rel +
                ", html='" + html + '\'' +
                '}';
    }

    public static class Meta {
        private String description;
        private String title;
        private String amphtml;
        private String copyright;
        private String keywords;
        private String media;
        @Json(name = "theme-color")
        private String color;
        private String canonical;
        private String site;

        public Meta(String description, String title, String amphtml, String copyright, String keywords, String media, String color, String canonical, String site) {
            this.description = description;
            this.title = title;
            this.amphtml = amphtml;
            this.copyright = copyright;
            this.keywords = keywords;
            this.media = media;
            this.color = color;
            this.canonical = canonical;
            this.site = site;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAmphtml() {
            return amphtml;
        }

        public void setAmphtml(String amphtml) {
            this.amphtml = amphtml;
        }

        public String getCopyright() {
            return copyright;
        }

        public void setCopyright(String copyright) {
            this.copyright = copyright;
        }

        public String getKeywords() {
            return keywords;
        }

        public void setKeywords(String keywords) {
            this.keywords = keywords;
        }

        public String getMedia() {
            return media;
        }

        public void setMedia(String media) {
            this.media = media;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getCanonical() {
            return canonical;
        }

        public void setCanonical(String canonical) {
            this.canonical = canonical;
        }

        public String getSite() {
            return site;
        }

        public void setSite(String site) {
            this.site = site;
        }

        @Override
        public String toString() {
            return "Meta{" +
                    "description='" + description + '\'' +
                    ", title='" + title + '\'' +
                    ", amphtml='" + amphtml + '\'' +
                    ", copyright='" + copyright + '\'' +
                    ", keywords='" + keywords + '\'' +
                    ", media='" + media + '\'' +
                    ", color='" + color + '\'' +
                    ", canonical='" + canonical + '\'' +
                    ", site='" + site + '\'' +
                    '}';
        }
    }

    public static class Links {
        private List<ThumbNail> thumbnail;
        private List<Icon> icon;
        private List<Player> player;
        private List<App> app;
        private List<Image> image;
        private List<Reader> reader;

        public List<App> getApp() {
            return app;
        }

        public List<Image> getImage() {
            return image;
        }

        public void setImage(List<Image> image) {
            this.image = image;
        }

        public List<Reader> getReader() {
            return reader;
        }

        public void setReader(List<Reader> reader) {
            this.reader = reader;
        }

        public void setApp(List<App> app) {
            this.app = app;
        }

        public List<Player> getPlayer() {
            return player;
        }

        public void setPlayer(List<Player> player) {
            this.player = player;
        }

        public List<ThumbNail> getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(List<ThumbNail> thumbnail) {
            this.thumbnail = thumbnail;
        }

        public List<Icon> getIcon() {
            return icon;
        }

        public void setIcon(List<Icon> icon) {
            this.icon = icon;
        }

        public Links(List<ThumbNail> thumbnail, List<Icon> icon) {
            this.thumbnail = thumbnail;
            this.icon = icon;
        }

        @Override
        public String toString() {
            return "Links{" +
                    "thumbnail=" + thumbnail +
                    ", icon=" + icon +
                    '}';
        }
    }

    public static class Reader {
        private String html;
        private List<String> rel;
        private String type;
        private ReaderMedia media;

        public String getHtml() {
            return html;
        }

        public void setHtml(String html) {
            this.html = html;
        }

        public List<String> getRel() {
            return rel;
        }

        public void setRel(List<String> rel) {
            this.rel = rel;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public ReaderMedia getMedia() {
            return media;
        }

        public void setMedia(ReaderMedia media) {
            this.media = media;
        }


        @Override
        public String toString() {
            return "Reader{" +
                    "html='" + html + '\'' +
                    ", rel='" + rel + '\'' +
                    ", type='" + type + '\'' +
                    ", media=" + media +
                    '}';
        }
    }

    public static class ReaderMedia {
        @Json(name = "max-widt")
        private int maxWidth;

        public int getMaxWidth() {
            return maxWidth;
        }

        public void setMaxWidth(int maxWidth) {
            this.maxWidth = maxWidth;
        }


        @Override
        public String toString() {
            return "ReaderMedia{" +
                    "maxWidth=" + maxWidth +
                    '}';
        }
    }

    public static class Image {
        private String type;
        private List<String> rel;
        private ImageMedia media;
        private String html;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<String> getRel() {
            return rel;
        }

        public void setRel(List<String> rel) {
            this.rel = rel;
        }

        public ImageMedia getMedia() {
            return media;
        }

        public void setMedia(ImageMedia media) {
            this.media = media;
        }

        public String getHtml() {
            return html;
        }

        public void setHtml(String html) {
            this.html = html;
        }

        @Override
        public String toString() {
            return "Image{" +
                    "type='" + type + '\'' +
                    ", rel=" + rel +
                    ", media=" + media +
                    ", html='" + html + '\'' +
                    '}';
        }
    }

    public static class ImageMedia {
        @Json(name = "aspect-ratio")
        private double ratio;
        private int width;
        private int height;

        public double getRatio() {
            return ratio;
        }

        public void setRatio(double ratio) {
            this.ratio = ratio;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        @Override
        public String toString() {
            return "ImageMedia{" +
                    "ratio=" + ratio +
                    '}';
        }
    }

    public static class AppMedia {
        @Json(name = "max-width")
        private int maxWidth;
        @Json(name = "aspect-ratio")
        private int aspectRatio;

        public int getMaxWidth() {
            return maxWidth;
        }

        public void setMaxWidth(int maxWidth) {
            this.maxWidth = maxWidth;
        }

        public int getAspectRatio() {
            return aspectRatio;
        }

        public void setAspectRatio(int aspectRatio) {
            this.aspectRatio = aspectRatio;
        }

        @Override
        public String toString() {
            return "AppMedia{" +
                    "maxWidth=" + maxWidth +
                    ", aspectRatio=" + aspectRatio +
                    '}';
        }
    }

    public static class App {
        private List<String> rel;
        private String type;
        private String html;
        private AppMedia media;

        public List<String> getRel() {
            return rel;
        }

        public void setRel(List<String> rel) {
            this.rel = rel;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getHtml() {
            return html;
        }

        public void setHtml(String html) {
            this.html = html;
        }

        public AppMedia getMedia() {
            return media;
        }

        public void setMedia(AppMedia media) {
            this.media = media;
        }

        @Override
        public String toString() {
            return "App{" +
                    "rel=" + rel +
                    ", type='" + type + '\'' +
                    ", html='" + html + '\'' +
                    ", media=" + media +
                    '}';
        }
    }

    public static class Player {
        private String type;
        private List<String> rel;
        private String href;
        private PlayerMedia media;
        private String html;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<String> getRel() {
            return rel;
        }

        public void setRel(List<String> rel) {
            this.rel = rel;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public PlayerMedia getMedia() {
            return media;
        }

        public void setMedia(PlayerMedia media) {
            this.media = media;
        }

        public String getHtml() {
            return html;
        }

        public void setHtml(String html) {
            this.html = html;
        }

        @Override
        public String toString() {
            return "Player{" +
                    "type='" + type + '\'' +
                    ", rel=" + rel +
                    ", href='" + href + '\'' +
                    ", media=" + media +
                    ", html='" + html + '\'' +
                    '}';
        }
    }

    static class PlayerMedia {
        @Json(name = "aspect-ratio")
        private double aspect;
        private String scrolling;


        public double getAspect() {
            return aspect;
        }

        public void setAspect(double aspect) {
            this.aspect = aspect;
        }

        public String getScrolling() {
            return scrolling;
        }

        public void setScrolling(String scrolling) {
            this.scrolling = scrolling;
        }

        @Override
        public String toString() {
            return "PlayerMedia{" +
                    "aspect=" + aspect +
                    ", scrolling='" + scrolling + '\'' +
                    '}';
        }
    }

    public static class ThumbNail {
        private String href;
        private String type;
        private List<String> rel;
        private Media media;

        public ThumbNail(String href, String type, List<String> rel, Media media) {
            this.href = href;
            this.type = type;
            this.rel = rel;
            this.media = media;
        }

        public Media getMedia() {
            return media;
        }

        public void setMedia(Media media) {
            this.media = media;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<String> getRel() {
            return rel;
        }

        public void setRel(List<String> rel) {
            this.rel = rel;
        }

        @Override
        public String toString() {
            return "ThumbNail{" +
                    "href='" + href + '\'' +
                    ", type='" + type + '\'' +
                    ", rel=" + rel +
                    '}';
        }
    }

    public static class Icon {
        private String href;
        private List<String> rel;
        private String type;
        private Media media;

        public Icon(String href, List<String> rel, String type, Media media) {
            this.href = href;
            this.rel = rel;
            this.type = type;
            this.media = media;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public List<String> getRel() {
            return rel;
        }

        public void setRel(List<String> rel) {
            this.rel = rel;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Media getMedia() {
            return media;
        }

        public void setMedia(Media media) {
            this.media = media;
        }

        @Override
        public String toString() {
            return "Icon{" +
                    "href='" + href + '\'' +
                    ", rel=" + rel +
                    ", type='" + type + '\'' +
                    ", media=" + media +
                    '}';
        }
    }

    public static class Media {
        private int width;
        private int height;

        public Media(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        @Override
        public String toString() {
            return "Media{" +
                    "width=" + width +
                    ", height=" + height +
                    '}';
        }
    }
}
