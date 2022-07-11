package course_project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import course_project.entity.Collection;
import course_project.entity.Comment;
import course_project.entity.ItemLike;
import course_project.entity.Tag;
import course_project.entity.field.Field;
import course_project.entity.item.Item;
import course_project.entity.item.Value;
import course_project.entity.user.User;
import course_project.payload.request.ValueDto;
import course_project.payload.response.ItemDto;
import course_project.payload.response.ItemInfoDto;
import course_project.repository.*;
import lombok.AllArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static course_project.entity.user.Role.ADMIN;

@Service
@AllArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final CloudinaryService cloudinaryService;
    private final ObjectMapper objectMapper;
    private final CollectionService collectionService;
    private final TagRepository tagRepository;
    private final FieldRepository fieldRepository;
    private final ValueRepository valueRepository;
    private final EntityManager entityManager;
    private final ItemLikeRepository itemLikeRepository;
    private final CommentRepository commentRepository;

    public void add(Long collectionId,
                    String name,
                    String tags,
                    MultipartFile image,
                    String fields) throws JsonProcessingException {

        String imageUrl = cloudinaryService.uploadFile(image);
        Collection collection = collectionService.authorize(collectionId);
        List<Tag> tagList = getTags(tags);
        Item item = new Item(name, imageUrl, collection, tagList);
        Item savedItem = itemRepository.save(item);
        List<Value> values = getValues(savedItem, fields);
        savedItem.setValues(values);
        itemRepository.save(savedItem);
    }

    private List<Tag> getTags(String tags){
        String[] tagArray = tags.split(" ");
        List<Tag> tagList = new ArrayList<>();
        for (String tag:tagArray) {
            tagList.add(getTag(tag));
        }
        return tagList;
    }

    private Tag getTag(String name) {
        name = name.replace("#","");
        Optional<Tag> optionalTag = tagRepository.findByName(name);
        if (optionalTag.isPresent())
            return optionalTag.get();
        Tag tag = new Tag(null,name,null);
        return tagRepository.save(tag);
    }

    public String getTagsByName(String name) throws JsonProcessingException {
        List<Tag> tags = tagRepository.getTagsByName(name);
        List<String> tagList = new ArrayList<>();
        for (Tag tag:tags)
            tagList.add(tag.getName());
        return objectMapper.writeValueAsString(tagList);
    }

    private List<Value> getValues(Item item, String values) throws JsonProcessingException {
        List<ValueDto> valueDtoList =  objectMapper.readValue(values, new TypeReference<>() {});
        List<Value> valueList = new ArrayList<>();
        for (ValueDto valueDto: valueDtoList){
            Value value = new Value(null, item,getField(valueDto.getId()),valueDto.getValue());
            valueList.add(valueRepository.save(value));
        }
        return valueList;
    }

    private Field getField(Long id){
        Optional<Field> field = fieldRepository.findById(id);
        if(field.isEmpty())
            throw new NullPointerException("Field not found");
        return field.get();
    }

    public String getItemsByCollection(Long collectionId) throws JsonProcessingException {
        List<Item> items = itemRepository.findAllByCollection_Id(collectionId);
        return convertItemsToString(items);
    }

    public String convertItemsToString(List<Item> items) throws JsonProcessingException {
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item:items){
            itemDtoList.add(new ItemDto(
                    item.getId(),
                    item.getName(),
                    getTagsAsString(item.getTags()),
                    item.getImageUrl()
            ));
        }
        return objectMapper.writeValueAsString(itemDtoList);
    }

    private String getTagsAsString(List<Tag> tags) {
        StringBuilder sb = new StringBuilder();
        for (Tag tag:tags){
            sb.append(" #");
            sb.append(tag.getName());
        }

        return sb.toString();
    }

    public String getItem(Long id) throws JsonProcessingException {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id,"Item"));
        List<Value> values = item.getValues();
        Long userId = item.getCollection().getUser().getId();
        ItemInfoDto itemDto = new ItemInfoDto(
                userId,item.getName(),
                getTagsAsString(item.getTags()),
                item.getImageUrl(),
                getValueDtoList(values));

        return objectMapper.writeValueAsString(itemDto);
    }

    public List<ValueDto> getValueDtoList(List<Value> values) {
        List<ValueDto> valueDtoList = new ArrayList<>();
        for(Value value:values){
            Field field = value.getField();
            valueDtoList.add(new ValueDto(
                    value.getId(),field.getName(),
                    field.getType().toString().toLowerCase(),value.getValue()));
        }
        return valueDtoList;
    }


    public void editItem(Long id, String name, String tags, MultipartFile image, String fields) throws JsonProcessingException {
        Item item = authorizeAndGet(id);
        item.setName(name);
        List<Tag> tagList = getTags(tags);
        String imageUrl = cloudinaryService.uploadFile(image);
        List<Value> values = editValues(fields);
        item.setValues(values);
        item.setImageUrl(imageUrl);
        item.setTags(tagList);
        itemRepository.save(item);
    }

    public Item authorizeAndGet(Long itemId){
        User principal = getPrincipal();
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ObjectNotFoundException(itemId,"Item"));
        Collection collection = item.getCollection();
        boolean isAuthorized = Objects.equals(collection.getUser().getId(), principal.getId())
                || principal.getRole() == ADMIN;
        if(!isAuthorized)
            throw new IllegalStateException("Not authorised");
        return item;
    }

    private List<Value> editValues(String values) throws JsonProcessingException {
        List<ValueDto> valueDtoList =  objectMapper.readValue(values, new TypeReference<>() {});
        List<Value> valueList = new ArrayList<>();
        for (ValueDto valueDto: valueDtoList){
            Value value = valueRepository.findById(valueDto.getId()).orElseThrow(() -> new ObjectNotFoundException(null,"Item"));
            value.setValue(valueDto.getValue());
            valueList.add(valueRepository.save(value));
        }
        return valueList;
    }

    public void like(Long id) {
        User principal = collectionService.getPrincipal();
        Item item = itemRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id,"Item"));
        Optional<ItemLike> optionalItemLike = itemLikeRepository.findByItem_IdAndUser_Id(item.getId(), principal.getId());
        if(optionalItemLike.isEmpty()){
            ItemLike itemLike = new ItemLike(null, principal, item);
            itemLikeRepository.save(itemLike);
        }else{
            ItemLike itemLike = optionalItemLike.get();
            itemLikeRepository.deleteById(itemLike.getId());
        }
    }

    public User getPrincipal(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Transactional
    public void deleteItem(Item item){
        List<Comment> comments = item.getComments();
        List<Value> values = item.getValues();
        valueRepository.deleteAll(values);
        commentRepository.deleteAll(comments);
        itemLikeRepository.deleteAllByItem_Id(item.getId());
        itemRepository.delete(item);
    }

    public void delete(Long id){
        Item item = itemRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, "Item"));
        Collection collection = item.getCollection();
        collectionService.authorize(collection.getId());
        deleteItem(item);
    }

    public void deleteCollection(Long id) {
        Collection collection = collectionService.authorize(id);
        List<Item> items = itemRepository.findAllByCollection_Id(id);
        for(Item item : items) deleteItem(item);
        collectionService.deleteCollection(collection);
    }

    public boolean checkLiked(Long id) {
        User principal = collectionService.getPrincipal();
        Item item = itemRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id,"Item"));
        Optional<ItemLike> optionalItemLike = itemLikeRepository.findByItem_IdAndUser_Id(item.getId(), principal.getId());
        return optionalItemLike.isPresent();
    }
}
