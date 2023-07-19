package ${package}.query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.z3950.zing.cql.CQLAndNode;
import org.z3950.zing.cql.CQLBooleanNode;
import org.z3950.zing.cql.CQLNode;
import org.z3950.zing.cql.CQLOrNode;
import org.z3950.zing.cql.CQLTermNode;

import eu.clarin.sru.server.SRUConstants;
import eu.clarin.sru.server.SRUException;
import eu.clarin.sru.server.fcs.parser.QueryParserException;

/**
 * Example query converter from CQL (BASIC search) to our own example
 * <em>MYQUERY</em> query language. The CQL search query will only use
 * Terms, and optionally AND/OR but no other CQL features with the FCS
 * BASIC search capability.
 */
public class CQLtoMYQUERYConverter {
    private static final Logger LOGGER = LogManager.getLogger(CQLtoMYQUERYConverter.class);
    
    public static String convertCQLtoMYQUERY(final CQLNode node)
            throws QueryParserException, SRUException {
        // LOGGER.debug("Query dump:\n{}", node.toXCQL());
        StringBuilder sb = new StringBuilder();

        convertCQLtoMYQUERYSingle(node, sb);

        return sb.toString();
    }

    /**
     * Recursive query converter since parsed queries are nested trees.
     * Uses a {@link StringBuilder} to generate the converted query output.
     */
    private static void convertCQLtoMYQUERYSingle(final CQLNode node, StringBuilder sb)
            throws SRUException {
        if (node instanceof CQLTermNode) {
            final CQLTermNode tn = ((CQLTermNode) node);
            if (tn.getIndex() != null && !"cql.serverChoice".equalsIgnoreCase(tn.getIndex())) {
                throw new SRUException(SRUConstants.SRU_CANNOT_PROCESS_QUERY_REASON_UNKNOWN,
                        "Queries with queryType 'cql' do not support index/relation on '"
                                + node.getClass().getSimpleName() + "' by this FCS Endpoint.");
            }
            sb.append('"');
            sb.append(tn.getTerm());
            sb.append('"');
        } else if (node instanceof CQLOrNode || node instanceof CQLAndNode) {
            final CQLBooleanNode bn = (CQLBooleanNode) node;
            if (!bn.getModifiers().isEmpty()) {
                throw new SRUException(SRUConstants.SRU_CANNOT_PROCESS_QUERY_REASON_UNKNOWN,
                        "Queries with queryType 'cql' do not support modifiers on '" + node.getClass().getSimpleName()
                                + "' by this FCS Endpoint.");
            }
            sb.append("( ");
            convertCQLtoMYQUERYSingle(bn.getLeftOperand(), sb);
            if (node instanceof CQLOrNode) {
                sb.append(" OR ");
            } else if (node instanceof CQLAndNode) {
                sb.append(" AND ");
            }
            convertCQLtoMYQUERYSingle(bn.getRightOperand(), sb);
            sb.append(" )");
        } else {
            throw new SRUException(SRUConstants.SRU_CANNOT_PROCESS_QUERY_REASON_UNKNOWN,
                    "Queries with queryType 'cql' do not support '" + node.getClass().getSimpleName()
                            + "' by this FCS Endpoint.");
        }
    }
}
