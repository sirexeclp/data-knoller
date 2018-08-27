package de.hpi.isg.dataprep.model.target;

import de.hpi.isg.dataprep.exceptions.PipelineSyntaxErrorException;
import de.hpi.isg.dataprep.model.repository.ErrorRepository;
import de.hpi.isg.dataprep.model.repository.MetadataRepository;
import de.hpi.isg.dataprep.model.repository.ProvenanceRepository;
import de.hpi.isg.dataprep.model.target.errorlog.ErrorLog;
import de.hpi.isg.dataprep.model.target.errorlog.PipelineErrorLog;
import de.hpi.isg.dataprep.util.TerminationCode;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Lan Jiang
 * @since 2018/6/4
 */
public class Pipeline extends PipelineComponent {

    private MetadataRepository metadataRepository;
    private ProvenanceRepository provenanceRepository;
    private ErrorRepository errorRepository;

//    private List<PipelineErrorLog> pipelineErrors;

    private List<Preparation> preparations;

    /**
     * The raw data contains a set of {@link Row} instances. Each instance represent a line in a tabular data without schema definition,
     * i.e., each instance has only one attribute that represent the whole line, including content and utility characters.
     */
    private Dataset<Row> rawData;

    private Pipeline() {
        this.metadataRepository = new MetadataRepository();
        this.provenanceRepository = new ProvenanceRepository();
        this.errorRepository = new ErrorRepository();
        this.preparations = new LinkedList<>();
    }

    public Pipeline(Dataset<Row> dataset) {
        this();
        this.rawData = dataset;
    }

    public void addPreparation(Preparation preparation) throws Exception {
        preparation.setPipeline(this);

        // build preparation, i.e., call the buildpreparator method of preparator instance to set metadata prerequiste and post-change
        preparation.getPreparator().buildMetadataSetup();

        this.preparations.add(preparation);
    }

    private void checkPipelineErrors() throws PipelineSyntaxErrorException {
        MetadataRepository metadataRepository = this.metadataRepository;
        preparations.stream().forEachOrdered(preparation -> preparation.checkPipelineErrorWithPrevious(metadataRepository));

        long numberOfPipelineError = errorRepository.getErrorLogs().stream()
                .filter(errorLog -> errorLog instanceof PipelineErrorLog)
                .map(errorLog -> (PipelineErrorLog) errorLog)
                .count();
        if (numberOfPipelineError > 0) {
            throw new PipelineSyntaxErrorException("The pipeline contains syntax errors.");
        }
    }

    public void executePipeline() throws Exception {
        try {
            checkPipelineErrors();
        } catch (PipelineSyntaxErrorException e) {
            // write the errorlog to disc.
            System.exit(TerminationCode.PIPELINE_HAS_ERROR);
        }
        for (Preparation preparation : preparations) {
            preparation.getPreparator().execute();
        }
    }

    public ErrorRepository getErrorRepository() {
        return errorRepository;
    }

    public MetadataRepository getMetadataRepository() {
        return metadataRepository;
    }

    public ProvenanceRepository getProvenanceRepository() {
        return provenanceRepository;
    }

    public Dataset<Row> getRawData() {
        return rawData;
    }

    public void setRawData(Dataset<Row> rawData) {
        this.rawData = rawData;
    }
}
