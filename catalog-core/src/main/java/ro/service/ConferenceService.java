package ro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ro.domain.*;
import ro.repository.*;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class ConferenceService {

    @Autowired
    private ConferenceRepository conferenceRepository;
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private CChairRepository cChairRepository;
    @Autowired
    private PcMemberRepository pcMemberRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private MyUserRepository userRepository;

    @Autowired
    private UserConferenceRepository userConferenceRepository;

    public ConferenceService() {
    }

    public List<Conference> getConferences(){return this.conferenceRepository.findAll();}

    public List<Section> getSections(){return this.sectionRepository.findAll();}

    public List<CChair> getCChairs(){return this.cChairRepository.findAll();}

    public List<UserConference> getUserConferences(){return this.userConferenceRepository.findAll();}

    @Transactional
    public void changeDeadlines(Long conferenceId, Date abstractDeadline, Date paperDeadline, Date bidDeadline, Date reviewDeadline, Date endDeadline, Date reEvalDate, Date submissionDate){
        this.conferenceRepository.findById(conferenceId).ifPresent(
                c -> {c.setAbstractDeadline(java.sql.Date.valueOf(abstractDeadline.getYear().toString()+'-'+abstractDeadline.getMonth().toString()+'-'+abstractDeadline.getDay().toString()));
                    c.setPaperDeadline(java.sql.Date.valueOf(paperDeadline.getYear().toString()+'-'+paperDeadline.getMonth().toString()+'-'+paperDeadline.getDay().toString()));
                    c.setBidDeadline(java.sql.Date.valueOf(bidDeadline.getYear().toString()+'-'+bidDeadline.getMonth().toString()+'-'+bidDeadline.getDay().toString()));
                    c.setReviewDeadline(java.sql.Date.valueOf(reviewDeadline.getYear().toString()+'-'+reviewDeadline.getMonth().toString()+'-'+reviewDeadline.getDay().toString()));
                    c.setEndingDate(java.sql.Date.valueOf(endDeadline.getYear().toString()+'-'+endDeadline.getMonth().toString()+'-'+endDeadline.getDay().toString()));
                    c.setReEvalDate(java.sql.Date.valueOf(reEvalDate.getYear().toString()+'-'+reEvalDate.getMonth().toString()+'-'+reEvalDate.getDay().toString()));
                    c.setSubmissionDate(java.sql.Date.valueOf(submissionDate.getYear().toString()+'-'+submissionDate.getMonth().toString()+'-'+submissionDate.getDay().toString()));
                }
        );
    }

    public Conference getConferenceFromId(Long id){
        return conferenceRepository.findById(id).orElse(null);
    }

    public Conference addConference(String name, Long chair_id, Long co_chair_id, Date startingDate, Date endingDate, Date abstractDeadline, Date paperDeadline,Date bidDeadline, Date reviewDeadline, Date reEvalDeadline, Date submissionDate){
        //
        return this.conferenceRepository.save(new Conference(name,
           java.sql.Date.valueOf(abstractDeadline.getYear().toString()+'-'+abstractDeadline.getMonth().toString()+'-' + abstractDeadline.getDay().toString()),
           java.sql.Date.valueOf(paperDeadline.getYear().toString()+'-'+paperDeadline.getMonth().toString()+'-' + paperDeadline.getDay().toString()),
           java.sql.Date.valueOf(bidDeadline.getYear().toString()+'-'+bidDeadline.getMonth().toString()+'-' + bidDeadline.getDay().toString()),
           java.sql.Date.valueOf(reviewDeadline.getYear().toString()+'-'+reviewDeadline.getMonth().toString()+'-' + reviewDeadline.getDay().toString()),
           java.sql.Date.valueOf(startingDate.getYear().toString()+'-'+startingDate.getMonth().toString()+'-' + startingDate.getDay().toString()),
           java.sql.Date.valueOf(endingDate.getYear().toString()+'-'+endingDate.getMonth().toString()+'-' + endingDate.getDay().toString()),
           java.sql.Date.valueOf(reEvalDeadline.getYear().toString()+'-'+reEvalDeadline.getMonth().toString()+'-' + reEvalDeadline.getDay().toString()),
           java.sql.Date.valueOf(submissionDate.getYear().toString()+'-'+submissionDate.getMonth().toString()+'-' + submissionDate.getDay().toString()),
           chair_id,co_chair_id));

    }


    public Date transformSQLDateIntoMyDate(java.sql.Date sqlDate){
        Calendar cal = Calendar.getInstance();
        cal.setTime(sqlDate);
        return new Date(cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.MONTH)+1,cal.get(Calendar.YEAR));
    }

    public Conference getConferenceFromName(String name){
        for(Conference conference: this.conferenceRepository.findAll()){
            if(conference.getName().equals(name)) return conference;
        }
        return null;
    }

    public void joinConference(long userId, long conferenceId){
        this.userConferenceRepository.save(new UserConference(conferenceId, userId));
    }

    public Author getAuthor(Long conferenceId, Long userId) {
        List<Author> authors = this.authorRepository.findAll().stream()
                .filter(author -> author.getConference_id().equals(conferenceId) && author.getUser_id().equals(userId))
                .collect(Collectors.toList());
        if (authors.isEmpty()) {
            return null;
        }
        return authors.get(0);
    }


    public Section addSection(Long conference_id, String username, String section_name) {
        if(conference_id==null) return null;
        MyUser user = userRepository.findAll().stream().filter(p->p.getUsername().equals(username)).findAny().orElse(null);
        if(user == null) return null;
        PcMember pcMember = pcMemberRepository.findAll().stream().filter(p->p.getUser_id().equals(user.getId())&&p.getConference_id().equals(conference_id)).findAny().orElse(null);
        CChair chair = cChairRepository.findAll().stream().filter(p->p.getUser_id().equals(user.getId())&&p.getConference_id().equals(conference_id)).findAny().orElse(null);
        if(pcMember == null && chair==null) return null;
        if(sectionRepository.findAll().stream().filter(s->s.getUser_id().equals(user.getId())&&s.getConference_id().equals(conference_id)).findAny().orElse(null)!=null)return null;
        return sectionRepository.save(new Section(user.getId(),conference_id,section_name));

    }

    public List<Section> getSectionsFromConference(Long conference_id){
        return sectionRepository.findAll().stream().filter(s->s.getConference_id().equals(conference_id)).collect(Collectors.toList());
    }
}