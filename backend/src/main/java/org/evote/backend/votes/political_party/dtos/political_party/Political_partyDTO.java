package org.evote.backend.votes.political_party.dtos.political_party;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Political_partyDTO {
    private Integer politicalPartyId;
    private String name;
    private Integer address_id;
}