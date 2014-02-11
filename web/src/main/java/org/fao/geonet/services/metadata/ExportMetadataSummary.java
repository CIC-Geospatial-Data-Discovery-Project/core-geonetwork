package org.fao.geonet.services.metadata;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.BinaryFile;
import org.apache.commons.io.IOUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.util.PriorityQueue;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.SelectionManager;
import org.fao.geonet.kernel.search.IndexAndTaxonomy;
import org.fao.geonet.kernel.search.SearchManager;
import org.jdom.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Export a summary.
 * <p/>
 * Created by Jesse on 2/11/14.
 */
public class ExportMetadataSummary implements Service {
    @Override
    public void init(String appPath, ServiceConfig params) throws Exception {

    }

    @Override
    public Element exec(Element params, ServiceContext context) throws Exception {
        GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
        final SearchManager searchManager = gc.getSearchmanager();
        final SelectionManager selectionManager = SelectionManager.getManager(context.getUserSession());

        IndexAndTaxonomy newIndexReader = null;

        final File summaryFile = File.createTempFile("summary", ".zip");
        ZipOutputStream out = null;
        try {
            out = new ZipOutputStream(new FileOutputStream(summaryFile));
            newIndexReader = searchManager.getNewIndexReader(context.getLanguage());

            ZipEntry entry = new ZipEntry("summary.csv");
            out.putNextEntry(entry);

            final IndexSearcher searcher = new IndexSearcher(newIndexReader.indexReader);

            final Set<FieldExporter> fieldExporters = new LinkedHashSet<FieldExporter>();
            fieldExporters.add(new NullWarningFieldExporter("_uuid", "Geonetwork UUID"));
            fieldExporters.add(new NullWarningFieldExporter("_id", "Geonetwork Internal ID"));
            fieldExporters.add(new NullWarningFieldExporter("_defaultTitle", "Default Title"));
            fieldExporters.add(new NullWarningFieldExporter("_title", "Title"));
            fieldExporters.add(new GroupOwnerFieldExporter(context));
            fieldExporters.add(new UserInfoFieldExporter());
            fieldExporters.add(new ValidFieldExporter());
            fieldExporters.add(new PublishedFieldExporter());
            fieldExporters.add(new TypeFieldExporter());

            final Set<String> fields = new LinkedHashSet<String>((int) (fieldExporters.size() * 0.5));

            for (FieldExporter fieldExporter : fieldExporters) {
                fields.add(fieldExporter.getFieldName());
            }

            StringBuilder builder = new StringBuilder();

            for (FieldExporter field : fieldExporters) {
                if (builder.length() > 0) {
                    builder.append(',');
                }
                builder.append(field.getFieldLabel());
            }

            builder.append('\n');
            out.write(builder.toString().getBytes("UTF-8"));
            final ZipOutputStream finalOut = out;

            final Set<String> selection = selectionManager.getSelection(SelectionManager.SELECTION_METADATA);

            final TopDocsCollector<ScoreDoc> results = new TopDocsCollector<ScoreDoc>(new PriorityQueue<ScoreDoc>(10) {
                @Override
                protected boolean lessThan(ScoreDoc a, ScoreDoc b) {
                    return false;
                }
            }) {
                public AtomicReaderContext context;

                @Override
                public void setScorer(Scorer scorer) throws IOException {
                    // ignore
                }

                @Override
                public void collect(int doc) throws IOException {
                    final Document document = context.reader().document(doc, fields);

                    for (FieldExporter fieldEntry : fieldExporters) {
                        fieldEntry.setFieldValue(document);
                    }
                }

                @Override
                public void setNextReader(AtomicReaderContext context) throws IOException {
                    this.context = context;
                }

                @Override
                public boolean acceptsDocsOutOfOrder() {
                    return true;
                }
            };

            for (String metadataUUID : selection) {
                searcher.search(new TermQuery(new Term("_uuid", metadataUUID)), results);


                builder.setLength(0);
                for (FieldExporter fieldExporter : fieldExporters) {
                    if (builder.length() > 0) {
                        builder.append(',');
                    }
                    builder.append(fieldExporter.getFieldValue());
                    fieldExporter.clear();
                }
                builder.append('\n');
                finalOut.write(builder.toString().getBytes("UTF-8"));
            }

            out.closeEntry();
            return BinaryFile.encode(200, summaryFile.getPath(), true);
        } finally {
            IOUtils.closeQuietly(out);
            searchManager.releaseIndexReader(newIndexReader);
        }
    }

    private static abstract class FieldExporter {
        private final String fieldName;
        private final String fieldLabel;
        String fieldValue;

        protected FieldExporter(String fieldName, String fieldLabel) {
            this.fieldName = fieldName;
            this.fieldLabel = fieldLabel;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String getFieldValue() {
            return fieldValue;
        }

        public String getFieldLabel() {
            return fieldLabel;
        }

        public void setFieldValue(Document document) {
            if (fieldValue == null) {
                this.fieldValue = document.get(fieldName);
            }
        }

        public void clear() {
            fieldValue = null;
        }
    }

    private class GroupOwnerFieldExporter extends FieldExporter {
        Map<String, String> idToName = new HashMap<String, String>();

        public GroupOwnerFieldExporter(ServiceContext context) throws Exception {
            super("_groupowner", "Owning Group");
            Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

            @SuppressWarnings("unchecked")
            final List<Element> users = dbms.select("SELECT id, name FROM Groups").getChildren();

            for (Element user : users) {
                this.idToName.put(user.getChildText("id"), user.getChildText("name"));
            }
        }

        @Override
        public String getFieldValue() {
            String rawValue = super.getFieldValue();

            if (rawValue == null) {
                return "No " + getFieldName() + " field in this document";
            }

            String mappedValue = idToName.get(rawValue);

            return mappedValue == null ? "Owning Group not in database.  Id was: " + rawValue : mappedValue;
        }
    }

    private class UserInfoFieldExporter extends FieldExporter {
        public UserInfoFieldExporter() {
            super("_userinfo", "Username, First, Last, Profile");
        }

        @Override
        public String getFieldValue() {
            return super.getFieldValue().replace('|', ',');
        }
    }

    private class ValidFieldExporter extends FieldExporter {
        public ValidFieldExporter() {
            super("_valid", "Validity");
        }

        @Override
        public String getFieldValue() {
            final String value = super.getFieldValue();
            if ("0".equals(value)) {
                return "Invalid";
            } else if ("1".equals(value)) {
                return "Valid";
            } else if ("2".equals(value)) {
                return "Invalid but Optional";
            }
            return value;
        }
    }

    private class PublishedFieldExporter extends FieldExporter {
        public PublishedFieldExporter() {
            super("_groupPublished", "Is Published");
        }

        @Override
        public String getFieldValue() {
            if ("1".equalsIgnoreCase(super.getFieldValue())) {
                return Boolean.TRUE.toString();
            } else {
                return Boolean.FALSE.toString();
            }
        }
    }

    private class TypeFieldExporter extends FieldExporter {
        public TypeFieldExporter() {
            super("_isTemplate", "Type (Metadata/Sub-template/Harvested");
        }

        @Override
        public String getFieldValue() {
            if ("n".equalsIgnoreCase(super.getFieldValue())) {
                return "Metadata";
            } else if ("y".equalsIgnoreCase(super.getFieldValue())) {
                return "Sub-template";
            } else if ("t".equalsIgnoreCase(super.getFieldValue())) {
                return "Harvested";
            } else {
                return "unknown";
            }
        }
    }

    private class NullWarningFieldExporter extends FieldExporter {
        public NullWarningFieldExporter(String fieldName, String fieldLabel) {
            super(fieldName, fieldLabel);
        }

        @Override
        public String getFieldValue() {
            final String value = super.getFieldValue();
            return value == null ? "Document does not contain the field " + getFieldName() : value;
        }
    }
}