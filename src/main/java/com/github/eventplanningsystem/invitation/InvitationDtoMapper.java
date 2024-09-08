package com.github.eventplanningsystem.invitation;

import java.util.function.Function;

public class InvitationDtoMapper implements Function<Invitation, InvitationDto> {
    @Override
    public InvitationDto apply(Invitation invitation) {
        return new InvitationDto(
                invitation.getId(),
                invitation.getEvent().getId(),
                invitation.getUser().getId(),
                invitation.getInvitationStatus()
        );
    }
}
