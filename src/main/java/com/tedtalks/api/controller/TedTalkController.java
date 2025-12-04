package com.tedtalks.api.controller;

import com.tedtalks.api.model.TedTalk;
import com.tedtalks.api.service.SpeakerAnalysisService;
import com.tedtalks.api.service.TedTalkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * REST controller for managing TedTalk resources.
 * <p>
 * Provides an end point to data import from a provided CSV file
 * Provides endpoints to perform CRUD operations
 * Provides an end point tp Speaker Influence analysis,
 * Provide an end point to query TedTalks by title or year.
 * </p>
 *
 * <ul>
 *     <li>GET /             - Retrieve all TedTalks</li>
 *     <li>GET /analysis     - Retrieve top N speaker titles</li>
 *     <li>GET /tedTalkPerYear?year=YEAR - Retrieve top speaker for a given year</li>
 *     <li>GET /getByTitle?title=TITLE - Retrieve a TedTalk by title</li>
 *     <li>POST /            - Add a new TedTalk</li>
 *     <li>PUT /{title}      - Update a TedTalk by title</li>
 *     <li>DELETE /{title}   - Delete a TedTalk by title</li>
 *     <li>POST /import      - To Import TedTalk data CSV file </li>
 *
 * </ul>
 *
 * This controller uses service TedTalkService for data management and
 * speakerAnalysisService for analyzing top talks and speakers.
 */
@RestController
@RequestMapping("/tedTalks")
public class TedTalkController {

    @Autowired
    private TedTalkService service;

    @Autowired
    private SpeakerAnalysisService speakerAnalysisService;

    /**
     * Retrieves all TedTalk objects.
     *
     * @return a list of all TedTalks
     */
    @GetMapping
    public List<TedTalk> getAll() {
        return service.getAll();
    }

    /**
     * Performs analysis to get the top N speaker titles.
     *
     * @param topNumber the number of top titles to retrieve
     * @return a list of maps containing speaker analysis results
     */
    @GetMapping("/analysis")
    public List<Map<String, String>> speakerAnalysis(@RequestParam("topNumber") int topNumber) {
        return speakerAnalysisService.getTopNTitles(getAll(), topNumber);
    }

    /**
     * Retrieves the top speaker/talk for a specific year.
     *
     * @param year the year to filter TedTalks by
     * @return a map containing the top talk information for the given year
     */
    @GetMapping("/tedTalkPerYear")
    public Map<String, String> speakerAnalysisPerYear(@RequestParam("year") Integer year) {
        List<TedTalk> tedTalkListPerYear = service.getTedTalksByYear(year);
        return speakerAnalysisService.getTopNTitles(tedTalkListPerYear, 1).get(0);
    }

    /**
     * Retrieves a TedTalk by its title.
     *
     * @param title the title of the TedTalk to retrieve
     * @return the matching TedTalk, or {@code null} if not found
     */
    @GetMapping("/getByTitle")
    public TedTalk getByTitle(@RequestParam("title") String title) {
        return service.findByTitle(title).orElse(null);
    }

    /**
     * Adds a new TedTalk to the system.
     *
     * @param t the TedTalk object to add
     * @return the added TedTalk
     */
    @PostMapping
    public TedTalk add(@RequestBody TedTalk t) {
        service.add(t);
        return t;
    }

    /**
     * Updates an existing TedTalk identified by its title.
     *
     * @param title   the title of the TedTalk to update
     * @param updated the TedTalk object containing updated values
     * @return "Updated" if the talk was found and updated, "Not found" otherwise
     */
    @PutMapping("/{title}")
    public String update(@PathVariable("title") String title, @RequestBody TedTalk updated) {
        boolean ok = service.update(title, updated);
        return ok ? "Updated" : "Not found";
    }

    /**
     * Deletes a TedTalk identified by its title.
     *
     * @param title the title of the TedTalk to delete
     * @return "Deleted" if the talk was removed, "Not found" otherwise
     */
    @DeleteMapping("/{title}")
    public String delete(@PathVariable("title") String title) {
        boolean ok = service.delete(title);
        return ok ? "Deleted" : "Not found";
    }


    /**
     * Import CSV file located on server filesystem (path parameter).
     * Example: POST /api/talks/import?path=/path/to/file.csv
     * Also supports importing the packaged resource 'data.csv' by passing path=classpath:data.csv
     */
    @PostMapping("/import")
    public String importCsv(@RequestParam("path") String path) {
        try {
            String actualPath = path;
            if (path.startsWith("classpath:")) {
                String resource = path.substring("classpath:".length());
                actualPath = new java.io.File(Objects.requireNonNull(getClass().getClassLoader().getResource(resource)).getFile()).getAbsolutePath();
            }
            int imported = service.importFromCsv(actualPath);
            return "Imported " + imported + " records from " + path;
        } catch (Exception e) {
            return "Import failed: " + e.getMessage();
        }
    }
}
