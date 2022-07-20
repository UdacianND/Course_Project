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
import course_project.payload.response.ItemsPage;
import course_project.repository.*;
import lombok.AllArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

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
        return Arrays.stream(tagArray).map(this::getTag)
                .collect(Collectors.toList());
    }

    private Tag getTag(String name) {
        name = name.replace("#","");
        return tagRepository.findByName(name)
                .orElse(tagRepository.save(new Tag(name)));
    }

    public String getTagsByName(String name) throws JsonProcessingException {
        List<String> tagList = tagRepository.getTagsByName(name).stream()
                .map(Tag::getName).collect(Collectors.toList());
        return objectMapper.writeValueAsString(tagList);
    }

    private List<Value> getValues(Item item, String values) throws JsonProcessingException {
        List<ValueDto> valueDtoList =  objectMapper.readValue(values, new TypeReference<>() {});
        return valueDtoList.stream().map(valueDto-> {
                    Field field = fieldRepository.findById(valueDto.getId()).orElseThrow();
                    return valueRepository.save(
                        new Value(item, field, valueDto.getValue()));
                }).collect(Collectors.toList());
    }


    public String getItemsByCollection(Long collectionId) throws JsonProcessingException {
        List<ItemDto> itemDtoList = itemRepository.findAllByCollection_Id(collectionId)
                .stream().map(this::getItemDto)
                .collect(Collectors.toList());
        return objectMapper.writeValueAsString(itemDtoList);
    }


    public ItemDto getItemDto(Item item){
        return new ItemDto(
                item.getId(),
                item.getName(),
                getTagsAsString(item.getTags()),
                item.getImageUrl());
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
        return values.stream().map(value ->{
                Field field = value.getField();
                return new ValueDto(
                        value.getId(),field.getName(),
                        field.getType().toString().toLowerCase(),
                        value.getValue());})
                .collect(Collectors.toList());
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
        return valueDtoList.stream().map(valueDto->{
            Value value = valueRepository.findById(valueDto.getId()).orElseThrow();
            value.setValue(valueDto.getValue());
            return valueRepository.save(value);
        }).collect(Collectors.toList());
    }

    public void like(Long id) {
        User principal = collectionService.getPrincipal();
        Item item = itemRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id,"Item"));
        itemLikeRepository.findByItem_IdAndUser_Id(item.getId(), principal.getId())
                .ifPresentOrElse(itemLikeRepository::delete, ()-> {
                            ItemLike itemLike = new ItemLike( principal, item);
                            itemLikeRepository.save(itemLike); });
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
        itemRepository.findAllByCollection_Id(id)
                .forEach(this::deleteItem);
        collectionService.deleteCollection(collection);
    }

    public boolean checkLiked(Long id) {
        User principal = collectionService.getPrincipal();
        Item item = itemRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id,"Item"));
        Optional<ItemLike> optionalItemLike = itemLikeRepository.findByItem_IdAndUser_Id(item.getId(), principal.getId());
        return optionalItemLike.isPresent();
    }

    public String getItemsByPage(Integer page, String searchString) throws JsonProcessingException {
        ItemsPage itemsPage;
        if(searchString == null)
            itemsPage = getItems(page);
        else
            itemsPage = searchItems(page, searchString);
        return objectMapper.writeValueAsString(itemsPage);
    }

    public ItemsPage getItems(Integer page){
        Pageable itemPage = PageRequest.of(page, 6, Sort.by("id").descending());
        Slice<Item> items = itemRepository.findAll(itemPage);
        boolean hasNext = items.hasNext();
        List<ItemDto> itemDtoList = new ArrayList<>();
        for(Item item:items){
            itemDtoList.add(getItemDto(item));
        }
        return new ItemsPage(itemDtoList, hasNext);
    }

    public ItemsPage searchItems(Integer page, String searchString){
        SearchSession searchSession = Search.session( entityManager );
        SearchResult<Item> result = searchSession.search( Item.class )
                .where( f -> f.match()
                        .fields( "name", "tags.name" ,"comments.content","values.value","collection.name")
                        .matching( searchString )).fetch( page*6, 6 );
        List<Item> items = result.hits();
        boolean hasNext = (page*6 + 6) < result.total().hitCount();
        List<ItemDto> itemDtoList = items.stream().map(this::getItemDto).collect(Collectors.toList());
        return new ItemsPage(itemDtoList, hasNext);
    }


    public String getTopTags() throws JsonProcessingException {
        Pageable limit = PageRequest.of(0, 6);
        List<String> tags = tagRepository.getTopTags(limit);
        return objectMapper.writeValueAsString(tags);
    }
}
