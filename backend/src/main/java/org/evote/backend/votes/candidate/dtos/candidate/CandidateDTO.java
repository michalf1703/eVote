package org.evote.backend.votes.candidate.dtos.candidate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.w3c.dom.Text;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CandidateDTO {
    private UUID candidate_id;
    private String name;
    private String surname;
    private Date birthDate;
    private String education;
    private String profession;
    private Long political_party_id;
    private Integer precinct_id;
    private Long election_id;
    private String info;
    private String image;

}