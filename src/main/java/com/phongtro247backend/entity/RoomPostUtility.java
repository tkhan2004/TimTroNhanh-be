package com.phongtro247backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "room_post_utilities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class RoomPostUtility {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rooms_id", referencedColumnName = "id")
    @ToString.Exclude
    private Room room;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utility_id", referencedColumnName = "id")
    @ToString.Exclude
    private RoomUtility utility;
}
