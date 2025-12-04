package com.tedtalks.api.service;

import com.tedtalks.api.model.TedTalk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TedTalkServiceTest {

    private TedTalkService service;

    @BeforeEach
    void setUp() {
        service = new TedTalkService();
    }

    @Test
    void testAddAndGetAll() {
        TedTalk t1 = new TedTalk("Title1", "Author1", "2020", 100, 10, "link1");
        TedTalk t2 = new TedTalk("Title2", "Author2", "2021", 200, 20, "link2");

        service.add(t1);
        service.add(t2);

        List<TedTalk> talks = service.getAll();
        assertEquals(2, talks.size());
        assertTrue(talks.contains(t1));
        assertTrue(talks.contains(t2));
    }

    @Test
    void testFindByTitle() {
        TedTalk t1 = new TedTalk("Title1", "Author1", "2020", 100, 10, "link1");
        service.add(t1);

        Optional<TedTalk> found = service.findByTitle("Title1");
        assertTrue(found.isPresent());
        assertEquals(t1, found.get());

        // Case-insensitive search
        found = service.findByTitle("title1");
        assertTrue(found.isPresent());

        // Non-existing title
        found = service.findByTitle("Unknown");
        assertFalse(found.isPresent());
    }

    @Test
    void testGetTedTalksByYear() {
        TedTalk t1 = new TedTalk("Title1", "Author1", "2020-05-01", 100, 10, "link1");
        TedTalk t2 = new TedTalk("Title2", "Author2", "2021-06-01", 200, 20, "link2");
        service.add(t1);
        service.add(t2);

        List<TedTalk> talks2020 = service.getTedTalksByYear(2020);
        assertEquals(1, talks2020.size());
        assertEquals(t1, talks2020.get(0));

        List<TedTalk> talks2021 = service.getTedTalksByYear(2021);
        assertEquals(1, talks2021.size());
        assertEquals(t2, talks2021.get(0));

        List<TedTalk> talks2019 = service.getTedTalksByYear(2019);
        assertTrue(talks2019.isEmpty());
    }

    @Test
    void testUpdate() {
        TedTalk t1 = new TedTalk("Title1", "Author1", "2020", 100, 10, "link1");
        service.add(t1);

        TedTalk updated = new TedTalk(null, "NewAuthor", "2021", 500, 50, "newLink");
        boolean result = service.update("Title1", updated);

        assertTrue(result);
        Optional<TedTalk> found = service.findByTitle("Title1");
        assertTrue(found.isPresent());
        TedTalk t = found.get();
        assertEquals("NewAuthor", t.getAuthor());
        assertEquals("2021", t.getDate());
        assertEquals(500, t.getViews());
        assertEquals(50, t.getLikes());
        assertEquals("newLink", t.getLink());

        // Updating non-existing talk
        boolean updateResult = service.update("Unknown", updated);
        assertFalse(updateResult);
    }

    @Test
    void testDelete() {
        TedTalk t1 = new TedTalk("Title1", "Author1", "2020", 100, 10, "link1");
        service.add(t1);

        boolean deleted = service.delete("Title1");
        assertTrue(deleted);
        assertTrue(service.getAll().isEmpty());

        // Delete non-existing talk
        boolean deleteResult = service.delete("Unknown");
        assertFalse(deleteResult);
    }

    @Test
    void testImportFromCsv() throws IOException {
        // Prepare a sample CSV file in src/test/resources
        File file = ResourceUtils.getFile("src/test/resources/test_data.csv");

        int count = service.importFromCsv(file.getAbsolutePath());
        assertTrue(count > 0);

        List<TedTalk> talks = service.getAll();
        assertEquals(count, talks.size());

        // Validate first record
        TedTalk t = talks.get(0);
        assertNotNull(t.getTitle());
        assertNotNull(t.getAuthor());
    }
}
