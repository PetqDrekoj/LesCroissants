package ro.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ro.converter.PaperConverter;
import ro.converter.PublishedPaperConverter;
import ro.domain.*;
import ro.dto.*;
import ro.service.ConferenceService;
import ro.service.EvaluationService;
import ro.service.MemberService;
import ro.service.PaperService;
import ro.utils.Message;

import java.util.ArrayList;
import java.util.List;

@RestController
public class PaperController {

    public static final Logger log = LoggerFactory.getLogger(PaperController.class);

    @Autowired
    private PaperService paperService;

    @Autowired
    private ConferenceService conferenceService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private PaperConverter paperConverter;

    @Autowired
    private PublishedPaperConverter publishedPaperConverter;

    @Autowired
    private EvaluationService evaluationService;

    @RequestMapping(value = "/papers", method = RequestMethod.GET)
    public List<PaperDto> getPapers() {
        return new ArrayList<PaperDto>(this.paperConverter.convertModelsToDtos(paperService.getPapers()));
    }

    @RequestMapping(value = "/published-papers", method = RequestMethod.GET)
    public List<PublishedPaperDto> getPublishedPapers() {
        return new ArrayList<PublishedPaperDto>(this.publishedPaperConverter.convertModelsToDtos(paperService.getPublishedPapers()));
    }

    @RequestMapping(value = "/abstract", method = RequestMethod.GET, params = {"conferenceName"})
    public List<AbstractDto> getAbstractsFromConferenceName(@RequestParam("conferenceName") String conferenceName) {
        Conference conference= this.conferenceService.getConferenceFromName(conferenceName);
        if(conference!=null){
            List<AbstractDto> abstracts = new ArrayList<>();
            paperService.getAbstracts().stream().filter(a->a.getConference_id()==conference.getId()).forEach(p->{
                Author author = memberService.getAuthorById(p.getAuthor_id());
                if(author!= null) {
                    MyUser user = memberService.getMemberFromId(author.getUser_id());
                    abstracts.add(new AbstractDto(p,user.getUsername()));
                }
            });
            return abstracts;
        }
        return null;

    }

    @RequestMapping(value = "/createabstract", method = RequestMethod.POST)
    public Message<String> addAbstract(@RequestBody CreateAbstractDto abstractDto) {
        MyUser user = memberService.getUserFromUsername(abstractDto.getUsername());
        Conference conference = conferenceService.getConferenceFromName(abstractDto.getConference_name());
        if (user!=null && conference!=null){
            Author author = this.memberService.addAuthor(user.getId(),conference.getId());
            try{
                this.paperService.addAbstract(abstractDto.getKeywords(),abstractDto.getTopics(),abstractDto.getTitle(),abstractDto.getAdditional_authors(),abstractDto.getContent(),author.getId(),conference.getId());
                return new Message<String>(null,"success");
            }
            catch(Exception e){
                return new Message<String>(null,"error");
            }
        }
        return new Message<String>(null,"error");
    }
    @Transactional
    @RequestMapping(value = "/editabstract", method = RequestMethod.POST)
    public Message<String> editAbstract(@RequestBody CreateAbstractDto abstractDto) {
        MyUser user = memberService.getUserFromUsername(abstractDto.getUsername());
        Conference conference = conferenceService.getConferenceFromName(abstractDto.getConference_name());
        if (user!=null && conference!=null){
            try{
                Abstract abs = paperService.getAbstracts().stream().filter(a-> memberService.getAuthorById(a.getAuthor_id()).getUser_id().equals(user.getId()) && a.getConference_id().equals(conference.getId())).findFirst().orElse(null);
                if(abs!=null) {
                    Abstract abstract_change = paperService.getAbstractById(abs.getId());
                    abstract_change.setContent(abstractDto.getContent());
                    abstract_change.setAdditionalAuthors(abstractDto.getAdditional_authors());
                    abstract_change.setKeywords(abstractDto.getKeywords());
                    abstract_change.setName(abstractDto.getTitle());
                    abstract_change.setTopics(abstractDto.getTopics());
                    return new Message<String>(null,"success");
                }
            }
            catch(Exception e){
                return new Message<String>(null,"error");
            }
        }
        return new Message<String>(null,"error");
    }



    @RequestMapping(value = "/createbid", method = RequestMethod.POST)
    public Message<String> addBid(@RequestBody CreateBidDto bidDto){

        log.trace("pana AICI");
        try{//Validation

            ///MAJOR warning: if pcMember belongs to more than one conference this is going to fail
            MyUser userId = memberService.getUserFromUsername(bidDto.getPc_name());
            PcMember pcMember = memberService.getPcMembers().stream().filter(p -> p.getUser_id().equals(userId.getId())).findAny().orElse(null);
            if(pcMember!= null) {
                Conference conference = conferenceService.getConferenceFromId(pcMember.getConference_id());

                Abstract abstrac = paperService.getAbstracts().stream().filter(a -> a.getId().equals(bidDto.getAbstract_id())).findAny().orElse(null);
                if(abstrac != null) {
                    if (conference != null && evaluationService.getBidEvaluations().stream().noneMatch(b -> b.getAbstract_id().equals(bidDto.getAbstract_id()) && b.getPc_id().equals(pcMember.getId()))) {
                        this.evaluationService.addBid(pcMember.getId(), bidDto.getAbstract_id(), bidDto.getResult(), bidDto.getDate());
                        return new Message<String>(null,"success");
                    }
                }
            }
        }
        catch (Exception e){
            return new Message<String>(null,"error");
        }
        return new Message<String>(null,"error");
    }



//    //INEFICIENT SEARCH
//    @RequestMapping(value = "/getbidpapers", method = RequestMethod.POST)
//    public List<Abstract> getBidPapers(@RequestBody String conference_name){
//        Conference conference= this.conferenceService.getConferenceFromName(conference_name);
//        if(conference!=null) {
//            List<Abstract> abstracts = new ArrayList<>();
//            List<BidEvaluation> bidEvaluations = this.evaluationService.getBidEvaluations().stream().filter(
//                    b -> paperService.getAbstracts().stream().anyMatch(
//                            p -> p.getId().equals(b.getAbstract_id()) && p.getConference_id().equals(conference.getId()))).collect(Collectors.toList());
//            HashMap<Long, Integer> abstractResult = new HashMap<>();
//            bidEvaluations.stream().forEach(p -> abstractResult.put(p.getAbstract_id(),p.getResult() + abstractResult.get(p.getAbstract_id())));
//            abstractResult.entrySet().stream().filter(a ->
//                    a.getValue() >= 0).forEach(a ->
//                    abstracts.add(paperService.getAbstracts().stream().filter(p ->
//                            p.getId().equals(a.getKey())).collect(toSingleton()))
//            );
//            return abstracts; //Convert to dto or die
//        }
//        return null;
//    }
}
