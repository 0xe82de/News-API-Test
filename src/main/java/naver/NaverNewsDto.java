package naver;

import lombok.Builder;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString(of = {"title", "originalLink", "link", "description", "publishDate"})
public class NaverNewsDto implements Comparable<NaverNewsDto> {

    private final String title;

    private final String originalLink;

    private final String link;

    private final String description;

    private final LocalDateTime publishDate;

    public LocalDateTime getPublishDate() {
        return publishDate;
    }

    @Builder
    public NaverNewsDto(String title, String originalLink, String link, String description, LocalDateTime publishDate) {
        this.title = title;
        this.originalLink = originalLink;
        this.link = link;
        this.description = description;
        this.publishDate = publishDate;
    }

    @Override
    public int compareTo(NaverNewsDto o) {
        return o.getPublishDate().compareTo(this.getPublishDate());
    }
}
