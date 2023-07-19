package ${package};

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ${package}.searcher.MyResults;
import eu.clarin.sru.server.SRUConstants;
import eu.clarin.sru.server.SRUDiagnostic;
import eu.clarin.sru.server.SRUDiagnosticList;
import eu.clarin.sru.server.SRUException;
import eu.clarin.sru.server.SRURequest;
import eu.clarin.sru.server.SRUSearchResultSet;
import eu.clarin.sru.server.SRUServerConfig;
import eu.clarin.sru.server.fcs.XMLStreamWriterHelper;


/**
 * A result set of a <em>searchRetrieve</em> operation. It it used to iterate
 * over the result set and provides a method to serialize the record in the
 * requested format.
 * <p>
 * A <code>SRUSearchResultSet</code> object maintains a cursor pointing to its
 * current record. Initially the cursor is positioned before the first record.
 * The <code>next</code> method moves the cursor to the next record, and because
 * it returns <code>false</code> when there are no more records in the
 * <code>SRUSearchResultSet</code> object, it can be used in a
 * <code>while</code> loop to iterate through the result set.
 * </p>
 * <p>
 * A required implemention for the target search engine.
 * </p>
 * <p>
 * This class only implements the minimal set of methods required to be
 * a valid implementation and to run.
 * </p>
 *
 * @see SRUSearchResultSet
 * @see <a href="http://www.loc.gov/standards/sru/specs/search-retrieve.html">
 *      SRU Search Retrieve Operation</a>
 */
public class ${classnameSRUSearchResultSet} extends SRUSearchResultSet {
    private static final Logger LOGGER = LogManager.getLogger(${classnameSRUSearchResultSet}.class);

    /**
     * SRU Server Config, might be useful for response generation?
     */
    SRUServerConfig serverConfig = null;
    /**
     * The SRU Request we generate our response for. Can be used to access
     * requested parameters.
     */
    SRURequest request = null;
    /**
     * The list of Data View identifiers we need to generate our response for.
     */
    private Set<String> extraDataviews;

    /**
     * Results wrapper container for easy access to metadata (total count etc.)
     */
    private MyResults results; // FIXME: change to correct result object
    /**
     * The record cursor position for iterating through the result set.
     */
    private int currentRecordCursor = 0;

    /**
     * Constructor.
     * 
     * @param serverConfig
     *            the {@link SRUServerConfig} object for this search engine
     * @param request
     *            the {@link SRURequest} with request parameters
     * @param diagnostics
     *            the {@link SRUDiagnosticList} object for storing
     *            non-fatal diagnostics
     * @param dataviews
     *            a list of String Data View identifiers to generate responses for.
     *            May be empty but must not be <code>null</code>.
     * @param results
     *            the actual results from the search engine
     */
    protected ${classnameSRUSearchResultSet}(SRUServerConfig serverConfig, SRURequest request,
            SRUDiagnosticList diagnostics, List<String> dataviews, MyResults results) {
        super(diagnostics);
        this.serverConfig = serverConfig;
        this.request = request;

        this.results = results;
        currentRecordCursor = -1;

        extraDataviews = new HashSet<>(dataviews);
    }

    /**
     * An identifier for the current record by which it can unambiguously be
     * retrieved in a subsequent operation.
     *
     * @return identifier for the record or <code>null</code> of none is
     *         available
     * @throws NoSuchElementException
     *             result set is past all records
     */
    @Override
    public String getRecordIdentifier() {
        return null;
    }

    /**
     * Serialize the current record in the requested format.
     *
     * @param writer
     *            the {@link XMLStreamException} instance to be used
     * @throws XMLStreamException
     *             an error occurred while serializing the result
     * @throws NoSuchElementException
     *             result set past all records
     * @see #getRecordSchemaIdentifier()
     */
    @Override
    public String getRecordSchemaIdentifier() {
        return request.getRecordSchemaIdentifier() != null ? request.getRecordSchemaIdentifier()
                : ${classnameConstants}.CLARIN_FCS_RECORD_SCHEMA;
    }

    /**
     * Get surrogate diagnostic for current record. If this method returns a
     * diagnostic, the writeRecord method will not be called. The default
     * implementation returns <code>null</code>.
     *
     * @return a surrogate diagnostic or <code>null</code>
     * @see <a href="https://github.com/clarin-eric/fcs-korp-endpoint/blob/ffccf7f65cc55744e1b1a8cebacce5485c530bda/src/main/java/se/gu/spraakbanken/fcs/endpoint/korp/KorpSRUSearchResultSet.java#L242-L253">
     *      Reference Implementation, Korp Endpoint</a>
     */
    @Override
    public SRUDiagnostic getSurrogateDiagnostic() {
        if ((getRecordSchemaIdentifier() != null) &&
                !${classnameConstants}.CLARIN_FCS_RECORD_SCHEMA.equals(getRecordSchemaIdentifier())) {
            return new SRUDiagnostic(
                    SRUConstants.SRU_RECORD_NOT_AVAILABLE_IN_THIS_SCHEMA,
                    getRecordSchemaIdentifier(),
                    "Record is not available in record schema \"" +
                            getRecordSchemaIdentifier() + "\".");
        }

        return null;
    }

    /**
     * The number of records matched by the query. If the query fails this must
     * be 0. If the search engine cannot determine the total number of matched
     * by a query, it must return -1.
     *
     * @return the total number of results or 0 if the query failed or -1 if the
     *         search engine cannot determine the total number of results
     */
    @Override
    public int getTotalRecordCount() {
        return (int) results.getTotal();
    }

    /**
     * The number of records matched by the query but at most as the number of
     * records requested to be returned (maximumRecords parameter). If the query
     * fails this must be 0.
     *
     * @return the number of results or 0 if the query failed
     */
    @Override
    public int getRecordCount() {
        return results.getResults().size();
    }

    /**
     * Moves the cursor forward one record from its current position. A
     * <code>SRUSearchResultSet</code> cursor is initially positioned before the
     * first record; the first call to the method <code>next</code> makes the
     * first record the current record; the second call makes the second record
     * the current record, and so on.
     * <p>
     * When a call to the <code>next</code> method returns <code>false</code>,
     * the cursor is positioned after the last record.
     * </p>
     *
     * @return <code>true</code> if the new current record is valid;
     *         <code>false</code> if there are no more records
     * @throws SRUException
     *             if an error occurred while fetching the next record
     */
    @Override
    public boolean nextRecord() throws SRUException {
        if (currentRecordCursor < (getRecordCount() - 1)) {
            currentRecordCursor++;
            return true;
        }
        return false;
    }

    /**
     * Serialize the current record in the requested format.
     *
     * @param writer
     *            the {@link XMLStreamException} instance to be used
     * @throws XMLStreamException
     *             an error occurred while serializing the result
     * @throws NoSuchElementException
     *             result set past all records
     * @see #getRecordSchemaIdentifier()
     */
    @Override
    public void writeRecord(XMLStreamWriter writer) throws XMLStreamException {
        MyResults.ResultEntry result = results.getResults().get(currentRecordCursor);

        XMLStreamWriterHelper.writeStartResource(writer, results.getPid(), null);
        XMLStreamWriterHelper.writeStartResourceFragment(writer, null, result.landingpage);

        // NOTE: we do not really have a KWIC result, so no left or right context.
        // We only return the hit ... Please improve.
        XMLStreamWriterHelper.writeHitsDataView(writer, "", result.text, "");

        // if (request != null && request.isQueryType("fcs" /* other query types */)) {
        //     writeXYZDataview(writer, result);
        // }

        XMLStreamWriterHelper.writeEndResourceFragment(writer);
        XMLStreamWriterHelper.writeEndResource(writer);
    }

    // protected void writeXYZDataview(XMLStreamWriter writer, MyResults.ResultEntry result) throws XMLStreamException {}
    
}
