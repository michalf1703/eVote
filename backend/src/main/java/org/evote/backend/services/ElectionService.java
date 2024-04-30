package org.evote.backend.services;

import org.evote.backend.votes.election.entity.Election;
import org.evote.backend.votes.election.exception.ElectionAlreadyExistsException;
import org.evote.backend.votes.election.exception.ElectionNotFoundException;
import org.evote.backend.votes.election.repository.ElectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ElectionService {
    @Autowired
    private ElectionRepository electionRepository;

    public List<Election> getAllElections() {
        return electionRepository.findAll();
    }
    public List<Election> getUpcomingElections() {
        List<Election> elections = electionRepository.findAll();
        LocalDate today = LocalDate.now();
        elections = elections.stream()
                .filter(election -> !election.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(today))
                .collect(Collectors.toList());

        if (!elections.isEmpty()) {
            LocalDate closestDate = elections.stream()
                    .map(election -> election.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                    .min(LocalDate::compareTo)
                    .orElse(today);

            elections = elections.stream()
                    .filter(election -> election.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().equals(closestDate))
                    .collect(Collectors.toList());
        }

        return elections;
    }
    public Election getElectionById(Long id) {
        return electionRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new ElectionNotFoundException("Election with id " + id + " not found"));
    }

    public Election addElection(Election election) {
        if (electionRepository.findById(Math.toIntExact(election.getElection_id())).isPresent()) {
            throw new ElectionAlreadyExistsException("Election with id " + election.getElection_id() + " already exists");
        }
        return electionRepository.save(election);
    }

    public void deleteElection(Long id) {
        Election election = electionRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new ElectionNotFoundException("Election with id " + id + " not found"));
        electionRepository.delete(election);
    }
}