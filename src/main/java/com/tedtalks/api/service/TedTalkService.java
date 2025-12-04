package com.tedtalks.api.service;

import com.tedtalks.api.model.TedTalk;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

@Service
public class TedTalkService {

    public static final String TITLE = "title";
    public static final String AUTHOR = "author";
    public static final String DATE = "date";
    public static final String VIEWS = "views";
    public static final String LIKES = "likes";
    public static final String LINK = "link";

    /**
     * Holds all TedTalk instances. The CopyOnWriteArrayList ensures
     * thread-safe reads and updates, maintaining the latest state across operations.
     */
    private final List<TedTalk> talks = new CopyOnWriteArrayList<>();


    /**
     * Retrieves all TedTalk objects.
     *
     * @return a list of all TedTalks
     */
    public List<TedTalk> getAll() {
        return talks;
    }

    /**
     * Retrieves the first TedTalk whose title matches the given input.
     * The comparison is case-insensitive and ignores null titles.
     *
     * @param title the title to search for (case-insensitive)
     * @return an Optional containing the matching TedTalk if found,
     *         otherwise an empty Optional
     */
    public Optional<TedTalk> findByTitle(String title) {
        return talks.stream()
                .filter(t -> t.getTitle() != null && t.getTitle().equalsIgnoreCase(title))
                .findFirst();
    }

    public List<TedTalk> getTedTalksByYear(Integer year) {
        return talks.stream()
                .filter(t -> t.getDate() != null && t.getDate().contains(year.toString()))
                .collect(Collectors.toList());
    }


    /**
     * Adds a TedTalk to the existing CSV data store.
     *
     * @param t TedTalk instance to be added
     */
    public void add(TedTalk t) {
        talks.add(t);
    }

    /**
     * Updates an existing TedTalk identified by its title.
     * <p>
     * The method searches for a talk matching the given title (case-insensitive).
     * If found, only the non-null fields of the provided updated object
     * are applied to the existing talk. Numeric fields such as views and likes
     * are always overwritten.
     * </p>
     *
     * @param title   the title of the TedTalk to update
     * @param updated the TedTalk object containing the new values to apply
     * @return true if the talk was found and updated; false otherwise
     */
    public boolean update(String title, TedTalk updated) {
        Optional<TedTalk> opt = findByTitle(title);
        if (opt.isEmpty()) return false;
        TedTalk t = opt.get();
        if (updated.getAuthor() != null) t.setAuthor(updated.getAuthor());
        if (updated.getDate() != null) t.setDate(updated.getDate());
        t.setViews(updated.getViews());
        t.setLikes(updated.getLikes());
        if (updated.getLink() != null) t.setLink(updated.getLink());
        return true;
    }

    /**
     * Deletes a TedTalk that matches the given title.
     * <p>
     * The comparison is case-insensitive and ignores null titles.
     * If one or more talks match the provided title, they are removed.
     * </p>
     *
     * @param title the title of the TedTalk to remove
     * @return true if at least one matching talk was removed;
     *         false otherwise
     */
    public boolean delete(String title) {
        return talks.removeIf(t -> t.getTitle() != null && t.getTitle().equalsIgnoreCase(title));
    }


    /**
     * Import talks data from a CSV file.
     * The CSV is expected to have header with columns title,author,date,views,likes,link
     */
    public int importFromCsv(String csvPath) throws IOException {
        Reader in = new InputStreamReader(new FileInputStream(csvPath), StandardCharsets.UTF_8);
        Iterable<CSVRecord> records = CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .parse(in);

        int count = 0;
        for (CSVRecord r : records) {
            String title = r.isMapped(TITLE) ? r.get(TITLE) : null;
            if (title == null || title.isBlank()) continue;
            String author = r.isMapped(AUTHOR) ? r.get(AUTHOR) : StringUtils.EMPTY;
            String date = r.isMapped(DATE) ? r.get(DATE) : StringUtils.EMPTY;
            long views = parseLongSafe(r.isMapped(VIEWS) ? r.get(VIEWS) : StringUtils.EMPTY);
            long likes = parseLongSafe(r.isMapped(LIKES) ? r.get(LIKES) : StringUtils.EMPTY);
            String link = r.isMapped(LINK) ? r.get(LINK) : StringUtils.EMPTY;
            TedTalk t = new TedTalk(title.trim(), author.trim(), date.trim(), views, likes, link.trim());
            talks.add(t);
            count++;
        }
        return count;
    }

    private long parseLongSafe(String s) {
        try {
            if (s == null || s.isBlank()) return 0L;
            String cleaned = s.replaceAll("[^0-9]", "");
            if (cleaned.isBlank()) return 0L;
            return Long.parseLong(cleaned);
        } catch (Exception e) {
            return 0L;
        }
    }
}
