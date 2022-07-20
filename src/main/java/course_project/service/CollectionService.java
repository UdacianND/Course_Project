package course_project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import course_project.entity.Collection;
import course_project.entity.Topic;
import course_project.entity.field.Field;
import course_project.entity.user.User;
import course_project.payload.request.CollectionFieldDto;
import course_project.payload.response.CollectionDto;
import course_project.payload.response.FieldDto;
import course_project.repository.CollectionRepository;
import course_project.repository.FieldRepository;
import course_project.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static course_project.entity.user.Role.ADMIN;

@Service
@RequiredArgsConstructor
public class CollectionService {
    private final CloudinaryService cloudinaryService;
    private final TopicRepository topicRepository;
    private final FieldRepository fieldRepository;
    private final ObjectMapper objectMapper;
    private final CollectionRepository collectionRepository;


    public void addCollection(
            String name,
            String topic,
            MultipartFile image,
            String description,
            String fields
    ) throws JsonProcessingException {
        String imageUrl = cloudinaryService.uploadFile(image);
        Collection collection = new Collection(
                name,
                description,
                getTopic(topic),
                imageUrl,
                getPrincipal()
        );
        Collection savedCollection = collectionRepository.save(collection);
        saveFields(savedCollection, fields);
    }

    private Topic getTopic(String name){
        return topicRepository.findByName(name)
                .orElse(topicRepository.save(new Topic(name)));
    }

    private void saveFields(Collection collection, String fields) throws JsonProcessingException {
        List<CollectionFieldDto> fieldList = objectMapper.readValue(fields, new TypeReference<>() {});
        fieldList.forEach( fieldDto ->{
            Field field = new Field(fieldDto, collection);
            fieldRepository.save(field);
        });
    }

    public User getPrincipal(){
        return (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public String getUserCollections() throws JsonProcessingException {
        Long userId = getPrincipal().getId();
        List<CollectionDto> collectionDtoList = collectionRepository.findAllByUserId(userId)
                .stream().map(collection -> new CollectionDto(
                                collection.getId(), userId,
                                collection.getName(),
                                collection.getTopic().getName(),
                                collection.getDescription(),
                                collection.getImageUrl()))
                .collect(Collectors.toList());
        return objectMapper.writeValueAsString(collectionDtoList);
    }

    public String getItemFields(Long collectionId) throws JsonProcessingException, IllegalStateException {
        authorize(collectionId);
        return objectMapper.writeValueAsString(getFields(collectionId));
    }

    public Collection authorize(Long collectionId){
        Collection collection = collectionRepository.findById(collectionId).orElseThrow();
        User principal = getPrincipal();
        boolean isAuthorized = Objects.equals(collection.getUser().getId(), principal.getId())
                || principal.getRole() == ADMIN;
        if(!isAuthorized)
            throw new IllegalStateException("Not authorised");
        return collection;
    }

    private List<FieldDto> getFields(Long collectionId){
        return fieldRepository.findAllByCollection_Id(collectionId)
                .stream().map(field ->  new FieldDto(
                        field.getId(),
                        field.getName(),
                        field.getType())).collect(Collectors.toList());
    }

    public String getCollectionInfo(Long collectionId) throws JsonProcessingException {
        Collection collection = collectionRepository.findById(collectionId).orElseThrow();
        CollectionDto collectionDto = getCollectionDto(collection);
        return objectMapper.writeValueAsString(collectionDto);
    }

    public CollectionDto getCollectionDto(Collection collection){
        return new CollectionDto(
                collection.getId(),
                collection.getUser().getId(),
                collection.getName(),
                collection.getTopic().getName(),
                collection.getDescription(),
                collection.getImageUrl());
    }

    public String getTopics(String name) throws JsonProcessingException {
        List<String> topicNames = topicRepository.getTopicsByName(name)
                .stream().map(Topic::getName)
                .collect(Collectors.toList());
        return objectMapper.writeValueAsString(topicNames);
    }

    public void editCollection(Long id, String name, String topicName, MultipartFile image, String description) {
        Collection collection = authorize(id);
        String imageUrl = cloudinaryService.uploadFile(image);
        Topic topic = getTopic(topicName);
        collection.setName(name);
        collection.setImageUrl(imageUrl);
        collection.setTopic(topic);
        collection.setDescription(description);
        collectionRepository.save(collection);
    }

    @Transactional
    public void deleteCollection(Collection collection){
        fieldRepository.deleteAllByCollection_Id(collection.getId());
        collectionRepository.delete(collection);
    }

    public String topCollections() throws JsonProcessingException {
        Pageable limit = PageRequest.of(0, 5);
        List<CollectionDto> collectionDtoList = collectionRepository.gelTopCollections(limit)
                .stream().map(this::getCollectionDto)
                .collect(Collectors.toList());
        return objectMapper.writeValueAsString(collectionDtoList);
    }
}
