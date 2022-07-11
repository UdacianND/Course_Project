package course_project.utils;

import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurationContext;
import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurer;
import org.springframework.stereotype.Component;

@Component("spring.jpa.properties.hibernate.search.backend.analysis.configurer")
public class CustomAnalyzer implements LuceneAnalysisConfigurer {
    @Override
    public void configure(LuceneAnalysisConfigurationContext context) {
        context.analyzer( "customAnalyzer" ).custom()
                .tokenizer( "standard" )
                .tokenFilter( "lowercase" )
                .tokenFilter( "snowballPorter" )
                .param( "language", "English" )
                .tokenFilter( "asciiFolding" );
    }
}
