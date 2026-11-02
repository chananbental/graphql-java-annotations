package graphql.annotations;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.SchemaPrinter;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.*;

/**
 * Smoke tests for annotation-generated schemas running on GraphQL Java 26.
 * Includes normal execution and schema export used by GraphQL code generators.
 */
public class GraphQLJava26CompatibilityTest {

    public static class Query {
        @GraphQLField
        public String greeting() {
            return "hello";
        }
    }

    @Test
    public void annotationGeneratedSchemaExecutes() {
        GraphQLSchema schema = AnnotationsSchemaCreator.newAnnotationsSchema()
                .query(Query.class)
                .build();

        ExecutionResult result = GraphQL.newGraphQL(schema)
                .build()
                .execute("{ greeting }");

        assertTrue(result.getErrors().isEmpty(), result.getErrors().toString());
        assertEquals(result.getData(), Map.of("greeting", "hello"));
    }

    @Test
    public void annotationGeneratedSchemaSupportsIntrospection() {
        GraphQLSchema schema = AnnotationsSchemaCreator.newAnnotationsSchema()
                .query(Query.class)
                .build();

        ExecutionResult result = GraphQL.newGraphQL(schema)
                .build()
                .execute("{ __schema { queryType { name } } }");

        assertTrue(result.getErrors().isEmpty(), result.getErrors().toString());
        Map<String, Object> data = result.getData();
        Map<String, Object> schemaData = (Map<String, Object>) data.get("__schema");
        Map<String, Object> queryType = (Map<String, Object>) schemaData.get("queryType");
        assertEquals(queryType.get("name"), "Query");
    }

    @Test
    public void annotationGeneratedSchemaCanBeExportedAsSdl() {
        GraphQLSchema schema = AnnotationsSchemaCreator.newAnnotationsSchema()
                .query(Query.class)
                .build();

        String sdl = new SchemaPrinter().print(schema);
        assertTrue(sdl.contains("type Query"), sdl);
        assertTrue(sdl.contains("greeting: String"), sdl);
    }
}
