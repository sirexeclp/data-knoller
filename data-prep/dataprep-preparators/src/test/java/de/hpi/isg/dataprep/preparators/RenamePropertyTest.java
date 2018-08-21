package de.hpi.isg.dataprep.preparators;

import de.hpi.isg.dataprep.exceptions.PreparationHasErrorException;
import de.hpi.isg.dataprep.implementation.defaults.DefaultRenamePropertyImpl;
import de.hpi.isg.dataprep.model.repository.ErrorRepository;
import de.hpi.isg.dataprep.model.target.Pipeline;
import de.hpi.isg.dataprep.model.target.Preparation;
import de.hpi.isg.dataprep.model.target.error.ErrorLog;
import de.hpi.isg.dataprep.model.target.error.PreparationErrorLog;
import de.hpi.isg.dataprep.model.target.preparator.Preparator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lan Jiang
 * @since 2018/8/19
 */
public class RenamePropertyTest {

    private static Dataset<Row> dataset;
    private static Pipeline pipeline;

    @BeforeClass
    public static void setUp() {
        Logger.getLogger("org").setLevel(Level.OFF);
        Logger.getLogger("akka").setLevel(Level.OFF);

        dataset = SparkSession.builder()
                .appName("Rename property unit tests.")
                .master("local")
                .getOrCreate()
                .read()
                .option("header", "true")
                .option("inferSchema", "true")
                .csv("./src/test/resources/pokemon.csv");
        pipeline = new Pipeline(dataset);
    }

    @Before
    public void cleanUpPipeline() throws Exception {
        pipeline = new Pipeline(dataset);
    }

    @Test
    public void testRenameExistingProperty() throws Exception {
        Preparator preparator = new RenameProperty(new DefaultRenamePropertyImpl());
        ((RenameProperty) preparator).setPropertyName("id");
        ((RenameProperty) preparator).setNewPropertyName("ID");

        Preparation preparation = new Preparation(preparator);
        pipeline.addPreparation(preparation);
        pipeline.executePipeline();

        List<ErrorLog> trueErrorLog = new ArrayList<>();
        ErrorRepository trueErrorRepository = new ErrorRepository(trueErrorLog);

        // First test error log repository
        Assert.assertEquals(trueErrorRepository, pipeline.getErrorRepository());

        Dataset<Row> updated = pipeline.getRawData();
        StructType updatedSchema = updated.schema();

        StructType trueSchema = new StructType(new StructField[] {
                new StructField("ID", DataTypes.StringType, true, Metadata.empty()),
                new StructField("identifier", DataTypes.StringType, true, Metadata.empty()),
                new StructField("species_id", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("height", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("weight", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("base_experience", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("order", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("is_default", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("date", DataTypes.StringType, true, Metadata.empty()),
        });

        // Second test whether the schema is correctly updated.
        Assert.assertEquals(trueSchema, updatedSchema);
    }

    /**
     * Rename non existing property should throw an exception.
     *
     * @throws Exception
     */
    @Test
    public void testRenameNonExistingProperty() throws Exception {
        Preparator preparator = new RenameProperty(new DefaultRenamePropertyImpl());
        ((RenameProperty) preparator).setPropertyName("Gaodu");
        ((RenameProperty) preparator).setNewPropertyName("gaodu");

        Preparation preparation = new Preparation(preparator);
        pipeline.addPreparation(preparation);
        pipeline.executePipeline();

        List<ErrorLog> trueErrorLog = new ArrayList<>();
        ErrorRepository trueErrorRepository = new ErrorRepository(trueErrorLog);
        ErrorLog errorLog = new PreparationErrorLog(preparation, "Gaodu", new PreparationHasErrorException("Property name does not exist."));
        trueErrorLog.add(errorLog);

        // First test error log repository
        Assert.assertEquals(trueErrorRepository, pipeline.getErrorRepository());

        Dataset<Row> updated = pipeline.getRawData();
        StructType updatedSchema = updated.schema();

        StructType trueSchema = new StructType(new StructField[] {
                new StructField("id", DataTypes.StringType, true, Metadata.empty()),
                new StructField("identifier", DataTypes.StringType, true, Metadata.empty()),
                new StructField("species_id", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("height", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("weight", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("base_experience", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("order", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("is_default", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("date", DataTypes.StringType, true, Metadata.empty()),
        });

        // Second test whether the schema is correctly updated.
        Assert.assertEquals(trueSchema, updatedSchema);
    }

    @Test
    public void testRenamePropertyToExistingName() throws Exception {
        Preparator preparator = new RenameProperty(new DefaultRenamePropertyImpl());
        ((RenameProperty) preparator).setPropertyName("order");
        ((RenameProperty) preparator).setNewPropertyName("weight");

        Preparation preparation = new Preparation(preparator);
        pipeline.addPreparation(preparation);
        pipeline.executePipeline();

        List<ErrorLog> trueErrorLog = new ArrayList<>();
        ErrorRepository trueErrorRepository = new ErrorRepository(trueErrorLog);
        ErrorLog errorLog = new PreparationErrorLog(preparation, "weight", new PreparationHasErrorException("New name already exists."));
        trueErrorLog.add(errorLog);

        // First test error log repository
        Assert.assertEquals(trueErrorRepository, pipeline.getErrorRepository());

        Dataset<Row> updated = pipeline.getRawData();
        StructType updatedSchema = updated.schema();

        StructType trueSchema = new StructType(new StructField[] {
                new StructField("id", DataTypes.StringType, true, Metadata.empty()),
                new StructField("identifier", DataTypes.StringType, true, Metadata.empty()),
                new StructField("species_id", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("height", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("weight", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("base_experience", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("order", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("is_default", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("date", DataTypes.StringType, true, Metadata.empty()),
        });

        // Second test whether the schema is correctly updated.
        Assert.assertEquals(trueSchema, updatedSchema);
    }
}