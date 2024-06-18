package org.evote.backend.services;

import org.evote.backend.users.account.entity.Account;
import org.evote.backend.users.account.exceptions.AccountNotFoundException;
import org.evote.backend.users.account.exceptions.UserAlreadyVotedException;
import org.evote.backend.users.account.repository.AccountRepository;
import org.evote.backend.votes.enums.CityType;
import org.evote.backend.users.user.entity.User;
import org.evote.backend.users.user.exceptions.CodeMismatchException;
import org.evote.backend.users.user.exceptions.UserNotFoundException;
import org.evote.backend.users.user.repository.UserRepository;
import org.evote.backend.votes.candidate.repository.CandidateRepository;
import org.evote.backend.votes.vote.dtos.SubmitVoteDTO;
import org.evote.backend.votes.candidate.entity.Candidate;
import org.evote.backend.votes.candidate.exception.CandidateWrongPrecinctException;
import org.evote.backend.votes.enums.CityType;
import org.evote.backend.votes.enums.ElectionType;
import org.evote.backend.votes.political_party.entity.PoliticalParty;
import org.evote.backend.votes.vote.dtos.SingleVoteDTO;
import org.evote.backend.votes.vote.dtos.VoteDTO;
import org.evote.backend.votes.vote.entity.Vote;
import org.evote.backend.votes.vote.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.sql.Time;

@Service
public class VotingService {

    private final AccountRepository accountRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final CandidateRepository candidateRepository;

    public VotingService(AccountRepository accountRepository, JwtService jwtService, UserRepository userRepository, VoteRepository voteRepository, CandidateService candidateService, CandidateRepository candidateRepository) {
        this.accountRepository = accountRepository;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
        this.candidateRepository = candidateRepository;
    }

    public String generateVotingToken(String email, String code) {
        Account account = accountService.getAccountByEmail(email).orElseThrow(() -> new AccountNotFoundException("Account not found"));
        User user = account.getUser();
        if (user == null) {
            throw new UserNotFoundException("User associated with this account not found");
        }
        if (!code.equals(user.getCode())) {
            throw new CodeMismatchException("Provided code does not match the user's code");
        }
        if (account.getHasVoted()) {
            throw new UserAlreadyVotedException("User has already voted");
        }
        return jwtService.generateVotingToken(account);
    }

    @Transactional(value = "chainedTransactionManager")
    public String vote(String token, VoteDTO voteDTO) {
        String email = jwtService.extractEmail(token);
        Optional<Account> account = accountService.getAccountByEmail(email);
        if (account.isEmpty()) {
            throw new AccountNotFoundException("Account not found");
        }
        Account dbAccount = account.get();
        User user = dbAccount.getUser();
        if (user == null) {
            throw new UserNotFoundException("User associated with this account not found");
        }
        if (isDataValid(dbAccount)) {
            List<SingleVoteDTO> votes = voteDTO.getVotes();
            for (SingleVoteDTO vote : votes) {
                Candidate candidate = candidateService.getCandidateById(vote.getCandidateId());
                if (!isValidPrecinct(user, candidate)) {
                    throw new CandidateWrongPrecinctException("Candidate not found");
                }
                Vote newVote = new Vote();
                newVote.setCandidate(candidate);
                newVote.setVoterBirthdate(user.getBirthDate());
                newVote.setVoterCityType(CityType.valueOf(user.getCityType().toString()));
                newVote.setVoterEducation(user.getEducation().toString());
                newVote.setSex(user.getSex());
                newVote.setVoterCountry(user.getAddress().getCountry());
                newVote.setVoteTime(Time.valueOf(LocalTime.now()));
                voteRepository.save(newVote);
            }
            dbAccount.setHasVoted(true);
            accountRepository.save(dbAccount);
            return "Voted successfully";
        }
        return "Voting failed";
    }

    public List<Vote> getResults() {
        List<Vote> votes = voteRepository.findAll();
        Map<PoliticalParty, Long> partyVotes = new HashMap<>();
        for(Vote vote : votes) {
            PoliticalParty party = vote.getCandidate().getPoliticalParty();
            if(partyVotes.containsKey(party)) {
                partyVotes.put(party, partyVotes.get(party) + 1);
            } else {
                partyVotes.put(party, 1L);
            }
        }
      
        User user = account.getUser();
        if (user == null) {
            throw new UserNotFoundException("User associated with this account not found");
        }
        if (user.getSex() == null || user.getAddress() == null || user.getPrecincts() == null || user.getName() == null
            || user.getSurname() == null || user.getBirthDate() == null || user.getPersonalIdNumber() == null
            || user.getEducation() == null || user.getCityType() == null || user.getProfession() == null) {
            throw new UserNotFoundException("User data is incomplete");
        }
        String token = jwtService.generateVotingToken(account);
        account.setHasVoted(true);
        accountRepository.save(account);
        return token;
    }

    public void submitVote(SubmitVoteDTO submitVoteDTO) {
        Vote vote = new Vote();
        vote.setVoter_birthdate(submitVoteDTO.getVoterBirthDate());
        vote.setVoter_city_type(submitVoteDTO.getVoterCityType());
        vote.setVoter_education(submitVoteDTO.getVoterEducation());
        vote.setVoter_country(submitVoteDTO.getVoterCountry());
        vote.setVote_time(new Time(System.currentTimeMillis()));
        vote.setCandidate(candidateRepository.findById(submitVoteDTO.getCandidateId())
                .orElseThrow(() -> new RuntimeException("Candidate not found")));

        voteRepository.save(vote);
    }

}