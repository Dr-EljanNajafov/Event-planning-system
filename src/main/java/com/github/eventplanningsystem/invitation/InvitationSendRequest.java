package com.github.eventplanningsystem.invitation;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvitationSendRequest {
    @NonNull
    private  Long eventId;
    @NonNull
    private Long userId;
}
