package com.phongtro247backend.controller;

import com.phongtro247backend.dto.ChatMessageResponse;
import com.phongtro247backend.dto.ChatThreadRequest;
import com.phongtro247backend.dto.ChatThreadResponse;
import com.phongtro247backend.payload.ApiResponse;
import com.phongtro247backend.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;


    @Operation(summary = "Bắt đầu một cuộc trò chuyện mới (hoặc lấy lại cuộc trò chuyện cũ)")
    @PostMapping("/threads")
    @PreAuthorize("hasRole('RENTER')") // Chỉ RENTER mới có thể bắt đầu cuộc trò chuyện
    public ResponseEntity<ApiResponse<ChatThreadResponse>> findOrCreateThread(
            @Valid @RequestBody ChatThreadRequest request) {
        try {
            // 1. User lấy từ SecurityContext [cite: RoomServiceImp.java]
            ChatThreadResponse thread = chatService.findOrCreateChatThread(request.getRoomId());

            // 2. Trả về response [cite: RoomController.java, ApiResponse.java]
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(201, "Tạo hoặc tìm thấy cuộc trò chuyện thành công", thread));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @Operation(summary = "Lấy danh sách các cuộc trò chuyện (inbox) của người dùng")
    @GetMapping("/threads")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<ChatThreadResponse>>> getMyThreads(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            // Sắp xếp theo "tin nhắn cuối cùng", nhưng tạm thời ta sort theo "ngày tạo thread"
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<ChatThreadResponse> threads = chatService.findChatThreads(pageable);

            return ResponseEntity.ok(
                    new ApiResponse<>(200, "Lấy danh sách trò chuyện thành công", threads)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @Operation(summary = "Lấy lịch sử tin nhắn của một cuộc trò chuyện")
    @GetMapping("/threads/{threadId}/messages")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<ChatMessageResponse>>> getMessagesForThread(
            @PathVariable Long threadId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            // Sắp xếp theo "sentAt" (thời gian gửi)
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sentAt"));
            Page<ChatMessageResponse> messages = chatService.findChatMessages(threadId, pageable);

            return ResponseEntity.ok(
                    new ApiResponse<>(200, "Lấy lịch sử tin nhắn thành công", messages)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }
}
