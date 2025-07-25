package searchengine.services.lemmaService;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.LuceneMorphology;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import searchengine.dto.dtoToBD.LemmaDto;
import searchengine.model.LemmaEntity;
import searchengine.repositories.LemmaRepository;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LemmaServiceImpl implements LemmaService {

    private final LuceneMorphology luceneMorphology;
    private final LemmaRepository lemmaRepository;
    private final ModelMapper modelMapper;

    //  protected static final Object lockLemmaRepository = new Object();


//    public LemmaServiceImpl(LuceneMorphology luceneMorphology, LemmaRepository lemmaRepository) {
//        this.luceneMorphology = luceneMorphology;
//        this.lemmaRepository = lemmaRepository;
//    }

    @Override
    public List<LemmaEntity> getAllLemmaEntities() {
        return List.of();
    }

    @Transactional
    @Override
    public LemmaDto getLemmaDtoById(int id) {
        //  synchronized (UtilitiesIndexing.lockLemmaRepository) {
        if (lemmaRepository.findById(id).isPresent()) {
            return modelMapper.map((lemmaRepository.findById(id).get()), LemmaDto.class);
        } else throw new RuntimeException("Lemma not found");
    }

    @Override
    public Set<LemmaDto> getSetLemmaDtoByLemmaWordForm(String lemmaWord) {
        Set<LemmaDto> lemmaDtoSet = new HashSet<>();
        Optional<Set<LemmaEntity>> optionalLemmaEntity = lemmaRepository.findByLemmaWord(lemmaWord);
        if (optionalLemmaEntity.isPresent()) {
            lemmaDtoSet = optionalLemmaEntity
                    .get()
                    .stream()
                    .map(lemmaEntity -> modelMapper.map(lemmaEntity, LemmaDto.class)).collect(Collectors.toSet());
        } /* а если леммы в БД нет, то вернем пустой Set<LemmaDto> lemmaDtoSet*/
        return lemmaDtoSet;
    }

    @Transactional
    @Override
    public LemmaDto saveLemmaDto(LemmaDto lemmaDto) {
        LemmaEntity lemmaEntity = modelMapper.map(lemmaDto, LemmaEntity.class);
        LemmaEntity savedLemmaEntity = lemmaRepository.save(lemmaEntity);
        return modelMapper.map(savedLemmaEntity, LemmaDto.class);
    }

    @Transactional
    @Override
    public boolean isPresentLemmaEntity(String lemma, int siteId) {
        //   synchronized (UtilitiesIndexing.lockLemmaRepository) {
        Optional<String> optionalLemma = lemmaRepository.findByLemma(lemma, siteId);
        return optionalLemma.isPresent();

    }

    @Transactional
    @Override
    public Optional<Integer> getLemmaId(String lemma, int siteId) {
        // synchronized (UtilitiesIndexing.lockLemmaRepository) {
        return lemmaRepository.findIdByLemma(lemma, siteId);
        // return optionalId.orElse(0);

    }

    @Override
    public List<String> getMorphologyFormsInfo(String word) {
        return luceneMorphology.getMorphInfo(word);
    }

    @Override
    public String getNormalBaseFormWord(String word) {  // выберем более близкую норм.форму из словаря
        List<String> wordNormalForms = luceneMorphology.getNormalForms(word.toLowerCase());
        String baseFormWord = "";
        for (String wordNormalForm : wordNormalForms) {
            if (wordNormalForm.compareTo(word) == 0) {
                baseFormWord = wordNormalForm;
                break;
            }
        }
        if (baseFormWord.isEmpty()) {  // это актуально к \word: иди\ (идти|a Г дст,пвл,2л,ед)
            baseFormWord = wordNormalForms.get(0);
        }
        return baseFormWord;

        //return (luceneMorphology.getNormalForms(word)).get(0);
    }

    @Override
    public boolean hasWordInDictionary(String word) {
        return luceneMorphology.checkString(word.toLowerCase()); // проверяем, является ли строка словом и
        // принадлежит язык слова выбранному словарю
    }

    @Override
    @Transactional
    public void deleteAllLemmaEntities() {
        Logger.getLogger(LemmaServiceImpl.class.getName()).info("Deleting all lemma entities   lemmaRepository.deleteAllLemmaEntity()");
        lemmaRepository.deleteAllLemmaEntity();
    }

    @Override
    @Transactional
    public void deleteLemmaEntityById(int lemmaId) {
        lemmaRepository.deleteById(lemmaId);
    }

    @Override
    @Transactional
    public void updateLemmaFrequency(List<Integer> idLemmaList) {
        for (Integer id : idLemmaList) {
            lemmaRepository.updateLemmaFrequency(id);

        }
    }

    @Override
    @Transactional
    public int getCountLemmasOfSite(int idSiteEntity) {
        return lemmaRepository.getCountLemmasWhereSiteId(idSiteEntity);
    }
}

