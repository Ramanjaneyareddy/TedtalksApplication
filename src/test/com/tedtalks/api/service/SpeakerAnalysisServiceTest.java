package com.tedtalks.api.service;

import com.tedtalks.api.model.TedTalk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SpeakerAnalysisServiceTest {

    private SpeakerAnalysisService service;

    @BeforeEach
    void setUp() {
        service = new SpeakerAnalysisService();
    }

    @Test
    void testComputeInfluenceScoresEmptyList() {
        List<TedTalk> talks = Collections.emptyList();
        Map<String, Double> scores = service.computeInfluenceScores(talks);
        assertTrue(scores.isEmpty(), "Scores should be empty for empty input list");
    }

    @Test
    void testComputeInfluenceScoresMultipleTalks() {
        TedTalk t1 = new TedTalk("Talk1", "Author1", "2020", 1000, 50, "link1");
        TedTalk t2 = new TedTalk("Talk2", "Author2", "2021", 2000, 30, "link2");
        TedTalk t3 = new TedTalk("Talk3", "Author3", "2022", 500, 70, "link3");

        List<TedTalk> talks = Arrays.asList(t1, t2, t3);
        Map<String, Double> scores = service.computeInfluenceScores(talks);

        assertEquals(3, scores.size());
        assertEquals(51.0, scores.get("Talk1"), 0.001);
        assertEquals(32.0, scores.get("Talk2"), 0.001);
        assertEquals(70.5, scores.get("Talk3"), 0.001);

        // Check that the entries are sorted descending by score
        Iterator<Map.Entry<String, Double>> it = scores.entrySet().iterator();
        assertEquals("Talk3", it.next().getKey());
        assertEquals("Talk1", it.next().getKey());
        assertEquals("Talk2", it.next().getKey());
    }

    @Test
    void testGetTopNTitles() {
        TedTalk t1 = new TedTalk("Talk1", "Author1", "2020", 1000, 50, "link1");
        TedTalk t2 = new TedTalk("Talk2", "Author2", "2021", 2000, 30, "link2");
        TedTalk t3 = new TedTalk("Talk3", "Author3", "2022", 500, 70, "link3");

        List<TedTalk> talks = Arrays.asList(t1, t2, t3);

        List<Map<String, String>> top1 = service.getTopNTitles(talks, 1);
        assertEquals(1, top1.size());
        Map<String, String> topMap1 = top1.get(0);
        assertTrue(topMap1.containsValue("Talk3"), "Top talk should be Talk3");

        List<Map<String, String>> top2 = service.getTopNTitles(talks, 2);
        Map<String, String> topMap2 = top2.get(0);
        assertEquals(2, topMap2.size());
        assertTrue(topMap2.containsValue("Talk3"));
        assertTrue(topMap2.containsValue("Talk1"));

        // Test topN greater than list size
        List<Map<String, String>> top5 = service.getTopNTitles(talks, 5);
        Map<String, String> topMap5 = top5.get(0);
        assertEquals(3, topMap5.size());
        assertTrue(topMap5.containsValue("Talk3"));
        assertTrue(topMap5.containsValue("Talk1"));
        assertTrue(topMap5.containsValue("Talk2"));
    }

    @Test
    void testGetTopNTitlesEmptyList() {
        List<TedTalk> talks = Collections.emptyList();
        List<Map<String, String>> top = service.getTopNTitles(talks, 3);
        assertEquals(1, top.size());
        assertTrue(top.get(0).isEmpty(), "Map should be empty for empty input list");
    }
}
