package com.phongtro247backend.service.ServiceImp;

import com.phongtro247backend.dto.*;
import com.phongtro247backend.entity.ChatMessage;
import com.phongtro247backend.entity.ChatThread;
import com.phongtro247backend.entity.Room;
import com.phongtro247backend.entity.User;
import com.phongtro247backend.repository.ChatRepository;
import com.phongtro247backend.repository.ChatThreadRepository;
import com.phongtro247backend.repository.RoomRepository;
import com.phongtro247backend.repository.UserRepository;
import com.phongtro247backend.service.ChatService;
import com.phongtro247backend.util.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatServiceImp implements ChatService {

    private final UserRepository userRepository;

    private final ChatRepository chatRepository;

    private  final RoomRepository roomRepository;

    private final ChatThreadRepository chatThreadRepository;

    private final SecurityUtil securityUtil;

    @Override
    public ChatThreadResponse findOrCreateChatThread(Long roomId) {
        User renter = securityUtil.getCurrentUser();

        Room room = roomRepository.findById(roomId)
                .orElseThrow(()-> new EntityNotFoundException("Không tìm thấy phòng"));

        if(room.getOwner().getId().equals(renter.getId())){
            throw new RuntimeException("Người dùng không thể tự trò chuyện với chính mình");
        }

        Optional<ChatThread> existingThread = chatThreadRepository.findByRoomAndRenter(room,renter);

        if(existingThread.isPresent()){
            return buildChatThreadResponse(existingThread.get(),renter);
        }else {
            ChatThread chatThread = ChatThread.builder()
                    .room(room)
                    .renter(renter)
                    .owner(room.getOwner())
                    .build();

            ChatThread saved = chatThreadRepository.save(chatThread);
            return buildChatThreadResponse(saved,renter);
        }
    }

    @Override
    public Page<ChatThreadResponse> findChatThreads(Pageable pageable) {
        User currentUser = securityUtil.getCurrentUser();

        Page<ChatThread> threads = chatThreadRepository.findByRenterOrOwner(currentUser,currentUser,pageable);

        return threads.map(thread -> buildChatThreadResponse(thread,currentUser));
    }

    @Override
    public Page<ChatMessageResponse> findChatMessages(Long id, Pageable pageable) {
        User currentUser = securityUtil.getCurrentUser();

        ChatThread thread = chatThreadRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Không tìm thấy cuộc trò chuyện"));

        if(thread.getOwner().getId().equals(currentUser.getId()) && !thread.getRenter().getId().equals(currentUser.getId())){
            throw new RuntimeException("Bạn không có quyền truy cập cuộc trò chuyện");
        }

        Page<ChatMessage> messages = chatRepository.findByThread(thread,pageable);
        return messages.map(this::buildChatMessageResponse);

    }

    @Override
    public ChatMessageResponse savedMessage(ChatMessageRequest chatMessageRequest, String senderMail) {
        User sender = userRepository.findByEmail(senderMail)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));

        ChatThread thread = chatThreadRepository.findById(chatMessageRequest.getThreadId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy cuộc trò chuyện"));

        if (!thread.getOwner().getId().equals(sender.getId()) &&
                !thread.getRenter().getId().equals(sender.getId())) {
            throw new RuntimeException("Bạn không có quyền truy cập cuộc trò chuyện");
        }

        ChatMessage message = ChatMessage.builder()
                .thread(thread)
                .sender(sender)
                .message(chatMessageRequest.getMessage())
                .build();

        ChatMessage saved = chatRepository.save(message);
        ChatMessageResponse response = buildChatMessageResponse(saved);

        User receiver = thread.getRenter().getId().equals(sender.getId()) ?
                thread.getOwner() : thread.getRenter();
        response.setReceiverEmail(receiver.getEmail());

        return response;
    }

    //HELPER METHOD
    private UserResponse buildUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setFullName(user.getFullName());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhone(user.getPhone());
        userResponse.setAvatarUrl(user.getAvatarUrl());
        userResponse.setRole(user.getRole());
        return userResponse;
    }

    private ChatMessageResponse buildChatMessageResponse(ChatMessage message) {
        return ChatMessageResponse.builder()
                .id(message.getId())
                .threadId(message.getThread().getId())
                .message(message.getMessage())
                .timestamp(message.getSentAt())
                .sender(buildUserResponse(message.getSender()))
                .build();
    }

    private ChatThreadResponse buildChatThreadResponse(ChatThread thread, User currentUser) {
        // 1. Xác định "người kia"
        User otherUser = thread.getRenter().getId().equals(currentUser.getId()) ?
                thread.getOwner() : thread.getRenter();

        // 2. Lấy thông tin phòng
        Room room = thread.getRoom();
        SimpleRoomResponse roomResponse = SimpleRoomResponse.builder()
                .id(room.getId())
                .title(room.getTitle())
                .address(room.getAddress())
                .build();

        // 3. Lấy tin nhắn cuối cùng (nếu có)
        ChatMessageResponse lastMessageResponse = null;
        Optional<ChatMessage> lastMessageOpt = chatRepository.findTopByThreadOrderBySentAtDesc(thread);
        if(lastMessageOpt.isPresent()){
            lastMessageResponse = buildChatMessageResponse(lastMessageOpt.get());
        }

        // 4. Build response
        return ChatThreadResponse.builder()
                .id(thread.getId())
                .otherUser(buildUserResponse(otherUser))
                .room(roomResponse)
                .lastMessage(lastMessageResponse)
                .build();
    }
}
