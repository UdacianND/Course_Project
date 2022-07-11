package course_project.utils;

import com.cloudinary.Cloudinary;
import com.fasterxml.jackson.databind.ObjectMapper;
import course_project.entity.Tag;
import lombok.RequiredArgsConstructor;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class BeanUtils {

    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Cloudinary cloudinaryConfig() throws InterruptedException {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "course-project-itransition");
        config.put("api_key", "455195617888756");
        config.put("api_secret", "pHOTu4FmeDJG7MRyixP7OTTUuPc");
        return new Cloudinary(config);
    }

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }



}
