package ${package}.searcher;

import java.util.List;

/**
 * Example <em>wrapper</em> around own, proprietary results.
 * 
 * Exposes metadata like the resource PID and search query as well as
 * result offset and total result count for pagination.
 * 
 * In particular the {@link ResultEntry} class needs to be customized
 * for the particular search engine backend in use.
 */
public class MyResults {
    private String pid;
    private String query;
    private List<ResultEntry> results;
    private long total;
    private long offset;

    public MyResults(String pid, String query, List<ResultEntry> results, long total, long offset) {
        this.pid = pid;
        this.query = query;
        this.results = results;
        this.total = total;
        this.offset = offset;
    }

    public String getPid() {
        return pid;
    }

    public String getQuery() {
        return query;
    }

    public List<ResultEntry> getResults() {
        return results;
    }

    public long getTotal() {
        return total;
    }

    public long getOffset() {
        return offset;
    }    

    /**
     * Minimal single result entry. Consisting of only a text and
     * backlink to the result (if available).
     */
    public static class ResultEntry {
        public String text;
        public String landingpage;

        public ResultEntry() {
        }
    
        @Override
        public String toString() {
            return "ResultEntry [text=" + text + "]";
        }
    }
}
