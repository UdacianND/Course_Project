//package course_project.utils;
//
//import course_project.entity.Collection;
//import course_project.entity.Comment;
//import course_project.entity.Tag;
//import course_project.entity.item.Item;
//import course_project.entity.item.Value;
//import course_project.entity.user.Role;
//import course_project.entity.user.User;
//import course_project.repository.TagRepository;
//import course_project.repository.UserRepository;
//import lombok.AllArgsConstructor;
//import lombok.SneakyThrows;
//import org.hibernate.search.mapper.orm.Search;
//import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
//import org.hibernate.search.mapper.orm.session.SearchSession;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//import org.springframework.transaction.annotation.Transactional;
//
//
//import javax.annotation.PostConstruct;
//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
//
//
//@Component
//@AllArgsConstructor
//@EnableTransactionManagement
//public class OnStartUpExecutor {
//    private  final UserRepository userRepository;
//    private  final EntityManagerFactory entityManagerFactory;
//    private  final TagRepository tagRepository;
//    private final PasswordEncoder passwordEncoder;
//
//
//    @PostConstruct
//    @Transactional
//    public void afterPropertiesSet() throws Exception {
//        EntityManager entityManager = entityManagerFactory.createEntityManager();
//        SearchSession searchSession = Search.session(entityManager);
//        System.out.println("start============================");
//        MassIndexer indexer = searchSession.massIndexer(Tag.class)
//                .threadsToLoadObjects(7);
//        indexer.startAndWait();
//        MassIndexer indexer4 = searchSession.massIndexer(Collection.class)
//                .threadsToLoadObjects(7);
//        indexer.startAndWait();
//        MassIndexer indexer5 = searchSession.massIndexer(Item.class)
//                .threadsToLoadObjects(7);
//        indexer5.startAndWait();
//
//        Tag tag1 = new Tag(null, "book", null);
//        Tag tag2 = new Tag(null, "books", null);
//        Tag tag3 = new Tag(null, "mybook", null);
//        Tag tag4 = new Tag(null, "java-book", null);
//        Tag tag5 = new Tag(null, "java", null);
//        tagRepository.save(tag1);
//        tagRepository.save(tag2);
//        tagRepository.save(tag3);
//        tagRepository.save(tag4);
//        tagRepository.save(tag5);
//
//        User user = new User("Azizbek", "jasur@gmail.com", passwordEncoder.encode("admin"), Role.ADMIN);
//        userRepository.save(user);
//    }
//}