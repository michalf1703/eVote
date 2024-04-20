package org.evote.backend.votes.candidate.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.evote.backend.votes.election.entity.Election;
import org.evote.backend.votes.political_party.entity.PoliticalParty;
import org.evote.backend.votes.precinct.entity.Precinct;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Table(name = "candidate")
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID candidate_id;

    private String name;

    private String surname;

    private Date birthDate;

    private String education;

    private String profession;

    @ManyToOne
    @JoinColumn(name = "political_party_id")
    private PoliticalParty politicalParty;

    @ManyToOne
    @JoinColumn(name = "precinct_id")
    private Precinct precinct;

    @ManyToOne
    @JoinColumn(name = "election_id")
    private Election election;
}