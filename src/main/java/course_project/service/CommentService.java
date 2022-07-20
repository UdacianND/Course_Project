package course_project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import course_project.entity.Comment;
import course_project.entity.item.Item;
import course_project.entity.user.User;
import course_project.payload.response.CommentDto;
import course_project.repository.CommentRepository;
import course_project.repository.ItemRepository;
import lombok.AllArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {
    private final SimpMessagingTemplate messagingTemplate;
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final ObjectMapper objectMapper;

    public void addComment(Long id, String content) throws JsonProcessingException {
        content =  objectMapper.readValue(content, new TypeReference<>() {});
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(id, "Item"));
        User principal = getPrincipal();
        Comment comment = new Comment(
                content,
                Time.valueOf(LocalTime.now()),
                principal,
                item
        );
        commentRepository.save(comment);
        CommentDto commentDto = new CommentDto(principal.getUsername(), content);
        messagingTemplate.convertAndSend("/item/"+id+"/comment",commentDto);
    }

    public User getPrincipal(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public String getComments(Long id) throws JsonProcessingException {
        List<CommentDto> commentDtoList = commentRepository.findAllByItem_Id(id).stream()
                .map(comment-> new CommentDto(
                        comment.getUser().getUsername(),
                        comment.getContent()))
                .collect(Collectors.toList());
        return objectMapper.writeValueAsString(commentDtoList);
    }
}
