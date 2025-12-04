package com.tedtalks.api.service;

import com.tedtalks.api.model.TedTalk;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Placeholder service for speaker influence analysis with basic metrics.
 */
@Service
public class SpeakerAnalysisService {

    /**
     * Compute a simple influence score for each talk:
     * score = likes + (views / 1000)
     * Returns a map of title -> score sorted descending.
     */
    public Map<String, Double> computeInfluenceScores(List<TedTalk> talks) {

        // Step 1: Build a HashMap of title → score
        Map<String, Double> scoreMap = new HashMap<>();
        for (TedTalk t : talks) {
            double score = t.getLikes() + (t.getViews() / 1000.0);
            scoreMap.put(t.getTitle(), score);
        }

        // Step 2: Convert entries to a list so we can sort
        List<Map.Entry<String, Double>> entries = new ArrayList<>(scoreMap.entrySet());

        // Step 3: Sort entries by score (value) in descending order
        entries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        // Step 4: Put sorted entries into a LinkedHashMap to preserve order
        Map<String, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    /**
     * Return top N talks by influence score.
     */
    public List<Map<String, String>> getTopNTitles(List<TedTalk> talks, int topN) {

        // First, compute the sorted influence scores map
        Map<String, Double> sortedScores = computeInfluenceScores(talks);

        List<Map<String, String>> result = new ArrayList<>();
        Map<String, String> sortedMap = new LinkedHashMap<>();

        int count = 0;
        for (Map.Entry<String, Double> entry : sortedScores.entrySet()) {
            if (count >= topN) {
                break;
            }

            count++;
            sortedMap.put("Top-"+count +" Ted Talk is: ", entry.getKey());
        }
        result.add(sortedMap);
        return result;
    }
}
