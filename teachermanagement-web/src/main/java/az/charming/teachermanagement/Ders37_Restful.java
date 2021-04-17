/* Postman Canary-ni yukledik ve daxil oldug( sign in olmurug cunki istifade limitini kecenden sonra pul isteyir).
Iceride ortada yuxarida  diger pencereleri bagladig,+ basirig untitled request adli pencere acildi (sonra adi
deyiseceyik), 'enter request URL' yerine url-imiz yazirig endpointimizde olan rest sutdens falan -
http://localhost:8080/rest/students ve send gonderdik asagida cavab gelir buda browserde olan cavabdi (json obyekti) ,
bunu etmekde meqsed her defe browsere girib javascriptle request gondermemek ucundu yeni burda yazdin yoxladin ve gordun
qaydasindadir. Sonra bu url-i save edirik cunki basqa urller falan istifade ede bilerik ve hacansa bu url lazim olar,
ctrl+s edirik acilan popopda request name yerine ad verdik (students-select-all meselen) ve bunu(requesti)
otuzduracagimiz yeri teyin edirik asagida 'create collection'-e basib qeyd edirik - students yazdig ve asagida save
etdik. Proyektde(StudentEndpoint de) bir dene apimiz var select apisidir ve indi basqa bir apide yazag meselen insert.
Hemenki proyketde ele elave edirik- StudentContorllerden add metodunu goturub StudentEndpointde yazirig bezi
deyisiklikler ederek(value-ni sildik, @ModelAttribute deyisib @RequestBody etdik, qebul etdiyi parametri -
StudentRequestDto studentRequestDto yazdig, StudentService de save metodu (bu da yazilacag) burda cagirdig ve icine
studentRequestDto-nun toStudentDto metodunu oturduk(bu da yazilacag icinde bir student duzeldilib), return-ne de
"success" yazdig-
@PostMapping
public String add(@RequestBody StudentRequestDto studentRequestDto){
        studentService.save(studentRequestDto.toStudentDto());
        return "success";
}
Burda da layer prinsipine fikir veririk. Sonra baxirig controllerimizdeki dto-nun claslarinin hamisi response-dur
demeli burda (dto-nun icinde) 2 dene paket acirig (response ve request adli). Response olan claslari response paketine
atirig ve request paketinde de request adli school, student, teacher dtolari acirig. Geldik StudentService clasinda save
metodu duzeltmeye-
public void save(StudentDto studentDto){
        studentRepository.save(studentDto.toEntity());
}
burda dto-dan entity duzeldib studentRepository-nin save-ne otururuk, bununcun StudentDto-nun icinde bele bir metod
yazirig hansi ki obyektin ozunu dto-dan entity duzeldib geriye qaytarir-
public StudentEntity toEntity(){
        List<TeacherEntity> teachers= new ArrayList<>();
        for(TeacherDto teacherDto: this.getTeacherList()) {
            teachers.add(new TeacherEntity()
                    .setName(teacherDto.getName())
                    .setId(teacherDto.getId())
            );
        }
        return new StudentEntity()
                .setAge(this.getAge())
                .setId(this.getId())
                .setName(this.getName())
                .setScholarship(this.getScholarship())
                .setSchool(
                        new SchoolEntity()
                                .setId(this.getSchool().getId())
                                .setName(this.getSchool().getName())
                )
                .setTeacherList(teachers)
                ;
}
Ancag evvelden SchoolEntity de ve diger claslarda set metodlari void ile idi qayidib onlari deyisib builder ile qururug
(hazir intellij de var eger olmasaydi muellim builderin icini atmisdi ozumuz orda + ile bir template acib hemen terkibi
bura atib istifade edeceydik) - SchoolDto, StudentDto,TeacherDto, SchoolEntity, StudentEntity, TeacherEntity ve
StudentRequestDto (deyisenlerini elave etdikden sonra) claslarinda cunki burda StudentEntity obyekti qurub ard arda .set
deye bilmek ucun.Gorunduyu kimi de teacherlist deyisenini yuxarda list duzeldib doldurub asagida set-e gonderdik. Save
getdi, bunu StudentEndpoint-de cagiririg yuxarda gosterdik , orda add metodu StudentRequestDto qebul edir ancag
studentService-in save-i ise StudentDto qebul edir onuncun StudentRequestDto-dan StudentDto duzeldirik.
StudentRequestDto - ya gelirik icerisi bosdur StudentDto-dan bu deyisenlerin copy-sini atirig bura -
public class StudentRequestDto {
    private Integer id;
    private String name;
    private String surname;
    private Integer age;
    private BigDecimal scholarship;

    private SchoolRequestDto school;
    private List<TeacherRequestDto> teacherList;

Burda school ve teacherlist deyisenlerinin tipine Request elave etdik.Elave Teacher ve School dtolari copy edib request
yazib adina atiriq request paketine. StudentRequestDto-nun getter setterlerini de acdig sonra StudentDto duzelden
metodumuzu yazirig-
public StudentDto toStudentDto(){
        List<TeacherDto> teacherDtos= new ArrayList<>();
        for(TeacherRequestDto teacherRequestDto: this.getTeacherList()){
            teacherDtos.add(new TeacherDto().setId(teacherRequestDto.getId())
            .setName(teacherRequestDto.getName())
            );
        }
        return new StudentDto()
                .setId(this.getId())
                .setName(this.getName())
                .setSurname(this.getSurname())
                .setAge(this.getAge())
                .setScholarship(this.getScholarship())
                .setSchool(
                        new SchoolDto()
                                .setId(this.getSchool().getId())
                                .setName(this.getSchool().getName())
                )
                .setTeacherList(teacherDtos)
                ;
    }
yene yuxarda TeacherList-i doldurub  add etdik. Demeli add metodunda (StudentEndpointde) requestden sade studentdto
duzeldib  qaytarir ve gedirik save metoduna sdudentdto-ya deyirik ki bunu entity-e cevir ve burada entity-ni
repository-e gonderib save buraxirig.Elimizle etdiklerimizi mapstruct adli bir sey var onunla edeceyik sadece daha yaxsi
basa dusmek ucun yazdig. Indi biz bu prosese diagramla baxag: 3 dene layerimiz var view, business, data layer (db laye).
Biz bayag postmanda api cagirdig ve dedik ki bu select elesin, indi ise teze yazdigimiz apini cagirmag istesek ki bu
gelib insert elesin bu zaman gelirik postmana solda olan students-select-all-un copysini cixardirig ve adini deyisib
students-add qoyurug, post edirik cunki proyektde @PostMapping yazilib (select de ise geti secmisik). Demeli select
edende- get, insert(add) - post, delete edende -delete, update edende - put secirik bu bir qaydadir (diegerleride var
lazim olsa google-da arasdirma etmek olar ancag en cox bunlar istifade edilir).Bunlar request gondermenin novleridir.
Insert ucun post secdikden sonra asagida body secirik body-nin altinda bir nece secim gelir raw secirik yeni ki el ile
ozumuz yazmag isteyirik raw bununcundur.Raw secende sagda text gelir burda da yazacagimiz yazinin tipini qeyd edirik,
json secdik.Sonra yazirig obyektimizi (iceride map eleyir ve onu insert edir) -
{
    "name": "Allahsukur",
    "surname": "Agazade",
    "age": 25,
    "scholarship": 300,
    "school": {
        "id": 1
    },
    "teacherlist": [
        {
            "id": 1
        },
        {
            "id": 2
        }
    ]
}
StudentRequestDto clasindaki deyisenlere uygun olarag doldurdug, teacherlist arraydir - [], icinde obyekt var - {}.
School ve teacherlerin idsini datadaki (workbenchde) melumatlara gore yazdig.Allahsukur adli telebenin butun melumatlari
yazildi, mektebini de, muellimlerini de qeyd etdik.
Indi layerleri niye ayirdig bunu izah edek- musteri gelir misalcun deyir teacherlistin (StudentRequestDto da) adini
deyisib teachers edek, gelib toStudentDTO metodunda da getTeachers edirik (evvel getTeacherlist idi), hemcinin get set
metodlari da teachers olur, ancag StudentDto-clasinda helede teacherlist-dir , deyismeyi de bizden teleb etmir ve bunun
hec bir problemi yoxdu cunki layerleri ayirmisig.Yeni musteri istedi bele gondersin , hmecinij giririk postmana bayag el
ile yazdigimiz (insert ucun) obyektde teacherlisti teachers edirik. Biz ancag view-da (request dto-da) isledik. business
layer (dto) ile, db layer(entity) ile isimiz olmadi.Kecen defe de business layer de ferqli is gormusduk getFullname
metodunu acmisdig tutalim yene edek adini defineFullname etdik burda olan deyisiklikler bu layer ucundu entity-e aid
deyil, texniki olarag bu metodu entity-de yaza bilerik ancag duzgun deyil(entity sabit olmalidir, ancag bazaya
baxmalidir, icinde elave sey olmamalidir). Ancag burda entity layer deyisiklik olsa business layere tesir edir cunki dto
entity-e gore formalasib.Dtolarin hamisi entity-e baxir deye bir sey yoxdu meselen bir vacation lazim olur duzeldirsen.
bunun entity ile elaqesi yoxdu sadece bu vacation obyektini doldurub gonderirsen hansisa metoda , bu metodda bu
datalardan istifade edib baslayir islemeye.Data oturmek ucun bir dene bean duzeldirsen ele bil ve duzeldib gonderirsen
qutardi getdi, iceride onu istifade edir(servicelerde onu istifade edir lazim gelenden gelene bazaya muraciet edir).
Indi baxag - dto da VacationSubmitDto clasi acdig 2 deyisen yazidig(submitter- vacation isteyen, reason) ve deyisenlerin
getter, setter(Builder il) metodlarini acdig:
public class VacationSubmitDto {
    private String submitter;
    private String reason;
Bunlarin hec kesle elaqesi yoxdu gelirsen sonra service paketine VacationService clasini acirig, ve studentService-e
nese insert elemek isteyirik tutalim hemen deyiseni yazirig ve burdaki metodla hemen dto clasini istifade edirik-
@Service
public class VacationService {
    private StudentService studentService;

    public void submitVacation(VacationSubmitDto vacationSubmitDto){
        studentService.save(new
                 StudentDto().setName(vacationSubmitDto.getSubmitter()));
    }
}
save metodu ile hemen telebeni yazdig bazaya.Ancag vacationsubmit-in bazayla elaqesi yoxdu sadece bu clasla elaqelidi
yeni kimse tetil goturmek istese vacationsubmit bu obyekti gonderirsen baslayir iceride islemeye. Bu dto - StudentDto
StudentService aiddir baza ile elaqeli dto-dur lakin bu dto- vacationsubmitdto xeyr. Adeten servicelerde iki hisseye
ayrilir bizde burda 2 dene paket acirig functional ve business . VacationService atirig business paketine,
StudentService-i ise functional paketine.StudentService sade servicedir(delete, insert, update ve s. bu isleri gorur)
ona gore buna functional deyilir. VacationService ise xususi business (is) gorur ona business deyirik.Bu serviceleri
ayirmayanda gelecekde isde cetinlik olur hansisa service axtaranda, bele ise hamisi ayri ayri seliqeli yigilir.Daha
gozel formasi , seliqeli formasi beledir ancag cox sirketde bu cur olmayada bilir, yeni acirig business de vacation
adli paket ve VacationService ile VacationSubmitDto(Dto paketinden cixarib )  claslarini bura atirig yeni ki vacation
aid olanlar bir yerde olur.Misalcun bu ise qebul ile baglida service paketi ola biler ve s. tipli. Burda layerleri
ayiririg ancag html formlarla isleyende, thymeleaf ile isleyende ehtiyac olmur cunki thymeleafde o derece get gel
yoxdur, yeni bu clasi (StudentController) cagiranda ozunsen, thymeleafden ne gelirse bura atirsan burdan thymeleafe
yazanda pozanda sensen. Ancag bayag ki apini frontend developer ayri cagiracag,  seninde ondan asliligin var deye onun
seliqe sehmanini fikirlesirsen (StudentEnpointde, bir url varsa ora request gedirse bur artig endpoint sayilir).
Url-de ? olursa query parametri, string parametri gedir (requestparamda gedir bu).
Html ile isleyende, thymeleaf isleyende - form data olur (browserde add (insert) olanda bu parametri istifade olunub).
Postman ve ya diger vasite ile json gondermek isteyende body atirsan bunu (postmanda) ve gonderiyin format json formati
olur. Add edende geden form data da eslinde request body-dir neye esasen postmanda body secende asagida raw kimi bir
nece secim var onlarda biri de form-data -dir.Ele add-de geden form data ya baxsag gorerik name:  bucur gedir yeni key
and value bu postmandaki form data-da beledir key var value var. Meselen yazirig form data secib name Sarkhan , surname
Rasullu ve headers baxsag gorerik ki (hiddene click edib) content type form data oldu, avtomatik biz secende ozu elave
eledi. Bu ne demekdir biz tomcata, application-imiza deyirik ki sene ne gonderirik neye esasen parse ele. Indi raw
secsek qayidib headers-e baxsag goreceyik content type json oldu. StudentEnpointde add metodunda iceri oturulen
parametrin onunde yazilan @RequesBody-ni istifade edirsen ki qarsi terefden gelen datanin mehz body-de oldugunu basa
dusursen ve deyirsen  men seni bu obyektin - StudentRequestDto icine dolduracam.Ona gore @requestbody deyirsen. Bu body
novleri postmanda hansi nov gonderirsense her biri ola biler.
Sual oluna biler ki niye kohne qayda da yeni thymeleaf ile isleyende niye bunu elememisik? ona gore ki thymeleafin
requesti qebul elemeyi ucun onun oz annotation-i var, StudentController-de @ModelAttribute(add, update metodlarinda) ile
ordan gelen butun datalari StudentEntity-e doldura bilir. Meselen url-den gelende @RequestParam istifade olunub (delete
metodunda) ve bu requestparam hemcinin StudentEndpointde de istifade oluna biler ona gore ki tema eynidir yeni sual
isaresi geldi ondan sonra ne yazirsansa bu requestparam sayilir ve bu annotasiya vasitesile onu tutursan. Istesen
@ModelAttribute yerine orda da @RequestBody yaza bilersen sadece bu zaman neyi itirirsen? uje thymeleaf sohbetinden
qiraga cixirsan yeni izah edek ne demek istediyimizi meselen index metodunda (StudentControllerde) modelin icine nese
doldura bilirik - model.addAttribute("list",list); Bunu dolduran zaman html terefde o bunu gore bilir ve goturub
istifade ede bilir, ancag requestbody yazsan bu zaman hemin anlayisdan artig qiraga cixirsan. Bele bir soz deyek
nehayetde sirf html ile isleyende, (mvc-de) spesifik olarag modelattribute istifade edirsiz, ona gore ki model-in diger
imkanlari da var hansi ki avtomatik olarag onu form data olarag basa dusur ve onu bura set edir (StudentEntity-e).
Requestbody yazsag ne bas verecek o zaman accaept deyilen bir sey olmalidir onu metodun uzerinde @PostMapping-in icinde
yazirsan deyirsen ki mene gelen data olacag form data. Onu artig requestbody bura doldurub size qaytara biler.
Prinsipce 2 si de eyni isi gore biler ferqine google da yazib baxmag olar. Bura(StudentControllerde)  requestbody yaza
bilersiz lakin endpoint(restful api) istifade edirsizse bura modelattribute yaza bilmezsiz cunki modelattribute dediyin
zaman mutleq html ile arasinda elaqe qurulur. Requestbody de ise html sohbeti yoxdu, sadece ferqi yoxdu requesti hardan
gonderirsen gonder (biz postmandan gonderirik), o, jsonu goturur ve bu obyektin (StudentRequestDto) icine doldurur.
Endpointin adinin ustunluyu (adinin nece olmasi) - ele bir api duzeltmek isteyirsiz hemin api vasitesile studentlerin
muellimlerin cekesiz meselen 1 nomreli studentin muellimlerin cekesiz. Buna friendly url deyilir ve qayda qoyulur ki
bele olsa gozel olar.Postmanda yazirig - http://localhost:8080/rest/students/1 slash 1 bize bir nomreli studenti
qaytarir, http://localhost:8080/rest/students/1/teachers bele slash 1 slash teachers yazanda 1 nomreli studentin
teacherlerini qaytarir. Bu bir razilasmadir, bir meslehetdir hansiki restful apini yaan sexslere deyirler ki bele
yazsaz yaxsi olar. Resurslari addelni bolun hemise, icaze vermeyin ki resurslar bir birinin icine girsin eks halda alem
deyecek bir birine.Bu ne demekdir meselen StudentEndpoint yazmisan bu studentlere xidmet edir , icaze verme ki bunun
icinde teacherler qayitsin yada teacherler insert, update olsun.Teacher ucun gotur bir dene TeacherController ,
TeacherEndpoint yaz ayrica, uzunlugu slash teachers olsun uzunlugu , teacher uzerinden insert-ni, update-ni elesin. Biri
var bayag ki url-de olan 1 nomreli studentin teacherlerini qaytariram bu ayri seydir yene de studente xidmet eleyirsen,
biri de var ki, sen meselen bele bir sey yazirsan - http://localhost:8080/rest/students/teachers studentin icinden
teacherleri qaytar bu zaman hec ne, alem deyir bir birine.Buna deyirler resusrlarin idare olunmasi ve resurlari mutleq
bir birinden ayirmag lazimdir.
Basqa bu url-de yazilan biri nece elde ede bilersiz buna baxag -  StudentEnpoint index metodunun uzerinde olan
RequestMapping deyisib - @GetMapping(value = "/students") edirik. Sonra getById metodunu acirig (StudentEndpointde) -
@GetMapping(value = "/students/{id}")
public StudentResponseDto getById(@PathVariable Integer id){
        return StudentResponseDto.instance(studentService.findById(id));
}
StudentServicede tebii ki findById metodunu qururuq burda cagira bilmekcun-

public StudentDto findById(Integer id){
        return StudentDto.instance(studentRepository.getOne(id));
}
Yuxarida PathVariable onunde- ("id) bele id de yaza bilerik ancag indi ehtiyac yoxdu ,cunki burda- "/students/{id}"
deyirem id qebul edecem ve qebul olan id gelib oturacag  Integer id bura. Bu bayag ki deyilen sohbet- urldeki bir yeni
bir nomreli studenti ver. Eger ikinci bir parametr olsaydi yaxud @PathVariable("id") burdaki id deyisenden ferqli
olsaydi yeni meselen Integer alma yazsaydig onda gerek PathVariable-in morterizesinde mutleq  qeyd edek ki bes axtarilan
id almaya otursun. Qisaca axtardigin ad ("/students/{id}") deyisenin adinda (Integer id) varsa pathvariable-in icinde
qeyd etmesek de olar.
Students-select-all duplicate edirik (postmanda) ve adini students-select-by-id yazirig ve url sonuna /1 yazirig
(requestin novu get olur) , select -all-un urlinden /1-i silirik. students-add bunun da students-insert edirik adini.
Indi ise delete-i qurag StudentEnpointde yazirig -
@DeleteMapping(value = "/students/{id}")
public void deleteById(@PathVariable Integer id){
      studentService.deleteById(id);
}
StudentServicede tebii ki deleteById metodunu qururug cagira bilmek ucun-

public void deleteById(Integer id){
     studentRepository.deleteById(id);
}
Indi de update ucun yazag , StudentEnpointde yazirig -
@PutMapping
public String update(@RequestBody StudentRequestDto studentRequestDto){
       studentService.save(studentRequestDto.toStudentDto());
       return "success";
}
StudentRequestDto  bunun icinde id var, obyektin icinde gonderilir url evezine ona gore putmapping-e value (id) vermeye
ehtiyac qalmir ve save cagirmag bes eleyir cunki save demisdik varsa update yoxdussa insert isini gorur. Sadece
StudentService de update metodu duzeldib if ile sert qoyursan orda da studentRepository-nin save-i cagirilir yeni bele -
public void update(StudentDto studentDto){
      if(studentRepository.getOne(studentDto.getId())==null)
            throw new IllegalArgumentException("not found");
      studentRepository.save(studentDto.toEntity());
}
Bu mecburiyyetdi cunki update sorgusu gonderen zaman yoxdusa insert eder alem deyer bir birine.Gorunduyu kimi eger
nulldursa exception atirig eks halda da update edirik.Postmanda da update acirig insertin eynisidi students-update adli.
Type-ni da put edirik icindeki json eynidi sadece evvele "id": 1, elave edirik.
Request gedir response gelir, response gelende http status gelir. Browserde http://localhost:8080/students/add bu urle
girib networkde baxsag gorerik add qirmizi rengde gelib ve Generalda status code : 500.  Ferqli-ferqli http kodlar var-
200 okey demekdir(her sey qaydasinda), 201 create demekdir (neyise insert elemisen), 404 not found , 500 daxili xeta
(daxilde her hansi xeta bas verib)  demekdir ve s. Bu kodlarin hamisini bilmek istesek HttpStatus clasina daxil olub
gore bilerik  (intellijde). meselen 201 de insert edende created qaytarmalisan. En cox istifade olunanlardan biri
bad_requestdir, yeni meselen postmanda json da age yerine yash yazib gondersek bad request verecek cunki kodda yash deye
bir field yoxdur o gedib o deyisene otura bilmir.Adindan da belli yeni request sehv gondermisen. Basqa biri meselen 5
nomreli useri update edirsen amma user yoxdur not found verir. Bu error codelar bosuna deyil bezen interviewda
sorusurlar meselen 404 neyi ifade edir yeni en cox lazim olanlara baxin. 502 bad gateway var sebekede problem olanda
meselen request gonderirsen gedib catmir.Bu saydiglarimiz esaslardi demek olar bunlari bilmek bes eder interview-da
status nedir bilirimi goren bu meqsedle sorusulur esasen advance seviyede sorusmurlar.
Restfulun ustunluyu birini dedik ki komanda bolgusunde faydalidir, frontend oz isini gorur, backend oz isini gorur ve s.
Diger terefden basqa bir ustunluyunu de fikirlesin meselen javada bir dene restful api yazmisan musteriye json qaytarir
ve siz ne ede bilersiz? bir dene mobil application hazilayirsiz yeni weble hec bir elaqesi yoxdu da, o mobil app sizin
restful apinizi cagirir, ordan datani alir ve istifade eleyir (telefonda hansisa datani ekrana cixarir, sen neyise daxil
edirsen o datani sizin apinize gonderir ve s.) belelikle ne etmis olursuz, mobilden hemin apini cagirarag bazaya neyise
yaza bilirsiniz eslinde en boyuk ustunluyu budur. Yeni ki platformalarin hec birinden asili olmursan sadece http request
gondermekle istediyin yere insert(add) eleye bilirsen.Bu aiddir her seye yeni lboy yerden sen requesti gondere bilirsen.
Belelikle html sehifesinden, browserden hec bir asililigin qalmir.
Diger terefden restful cox yungul isleyir, yeni biri var html sehifeni acasan zirilti seyler yuklene (rengler, html-ler
ve s.), biri de var kicik bir json select olur qutardi getdi.
Restful ile diger nov sayt hazirlamag arasinda ferq- restful statelessdir, digeri  ise stateful-dur (state- hal,
veziyyet ve s demekdir). Sen bir html sehifesi acdigin zaman thymeleaf ile, formlar ile, mvc ile application yazdigin
zaman seninle tomcat arasinda bir elaqe olur, bu elaqe nedir?- meselen girmisik sayta, bu sayt ile artig menim tomcatim
arasinda bir elaqe var. Meni tomcat bir qeydiyyata alir ve meni hemin o qeydiyyat nomresine esasen taniyir. Sabahlari o
meselen mene aid arxada yaddasda bir obyekt saxlayar ve hemin obyekti tutaq ki istifade ede biler, bu usula deyirler
stateful. Yeni sene aid olan ne varsa o ozunde saxlayir ve track edir (izleyir). Ne vaxt ki sen x basib cixdin bu zaman
state ölür, aramizda artig bir elaqe qalmir ona gore buna stateful deyilir. Restful api ise statelessdir- yeni bu apinin
senle arasinda hec bir elaqe yoxdur, deyir meselen insert elemek isteyirsen ? insert-i eledin cixdin getdin. Onun icinde
sene aid hec bir state yoxdur, onun state-i hec zaman deyirmeyir. Bu da ustunlukdur eslinde.Yeni light weight (yungul)
isleyir. Bu interviewlarda sorusulan en vacib sualdir , ozde bele sorusurlar restful ile traditional web apin ferqi
nedir? Cavab- restful statelessdir, amma mvc ile, web formlarla yazilan applicationlarsa statefuldur. Stateful- web
browser ile application arasinda bir state qurulur, sesiya qurulur ve siz applicationi saxlyana kimi, browserden cixana
kimi bu sesiya oz aktivliyini saxlayir. Bezen yadiniza salsaz gorersiz ki, bir sayta girirsiz gorursuz sizi taniyir,
avtomatik sisteme daxil olur, yaxudda ki melumatlariniz ekranda cixdi ve s . yeni aranizda bir sesiya var ve bu sesiya
aciqdir.
Restfulun yazilmasi- restfulu biz ele goturub springde yazirig amma restful mehz springde yazirlar deye bir anlayis
yoxdur. Cunki restful demek eslinde sadece request gondrilib response alina bilen, requestde json gonderilen yaxud
response json alina bilen, http uzerinden gonderile bilen sadece bir anlasmadir. Burda hec bir qayda qanun yoxdur ki bu
bele olmalidir, sadece bir anlasmadir. Ona gore sizden sorusulsa ki  spring olmasa, java olmasa restful yazilarmi? tebii
ki yazilar. Restful protocol deyil, bunu da sorusurlar , restful convention-lardir. Niye cunki seni hec kim mecbur
etmir, sen  tutalim postmanda url-de ...students/select-all belede yaza bilersen, ygoturersen metodun basina
(StudentEndpoint-de index-in) getin value-suna /students/select-all da yazarsan tutaq ki. Bu bir anlasmadir ki yeni
deyir neyinize lazimdir select all yazirsiz onsuz get edende biz basa dusuruk ki sen her seyi get edirsen yaxudda id
gore isteyirsense gotur arxasina id yaz ("/students/{id}"). Conventionlar ile yazilmis bir apidir. Json yerine xml-de
yazib gondere bilirsiz meselen xml secib yazirig-
<student>
    <id>1</id>
    <name>asjdn</name>
    <teachers>
        <teacher>
            <id></id>
        </teacher>
    </teachers>
</student>
yuxarda gorunduyu kimi studentin name-i, id-si var, teacherlisti var, teachlistin obyekti onunda id-si var bele bir sey
tutalim. Bilmek lazimdir ancag cox adam xml istifade etmir (nadir hallarda olur). En mehsuru, en gozeli ele json-dur.
Cunki web browseri jsonu avtomatik taniyir amma xml ile web browserde rahat isleye bilmirsiz.Qisasi xml bas agrisidir.
Restful api lerde bayag dediyimiz sey - postmanda urle arxasina action (meselen select-all) yazmag duzgun sayilmir. Cox
spesifik bir hadise olarsa meselen url-de yazdig ...students/1/submit-scholarship yeni students apisinde 1 nomreli
student scholarshipe muraciet edir. Bu halda olar cunki basqa bir cixis yolu qalmir. Update , delete , select, insert
bunlarda xusui bir ad yazmag lazim deyil ancag bele bir hallarda olar. Bu xususi halda post emeliyati secirik cunki
submit bazaya gedib nese oturacag, insert edecek. "Security-ni juniordan sorusmurlar, oyreneceyiniz secutiry-ni hec isde
istifade etmirsiniz cunki onlarin oz tool-lari var."
Gelirik select-all-a sagda no enviroment var  orda goze click edib, enviroment add vurub yazirig- new enviroment yerine-
local yazdig, variable yerine - student-host(serti verilen addir istdeyinizi yaza bilersiz) yazdig, initial value yerine
- http://localhost:8080 yazirig bura yazdigimiz avtomatik  current value yerine de yazilir ve save basib qeyd etdik. Bu
bize ne imkan verir- gelirik select-all da url-e gelib student-host yazirig  bu sekilde - {{student-host}}/rest/students
ve sag terefde de no enviroment yox local secirik. Sonra mouse uzerine getirende goruruk ki http://localhost:8080 falan
gelir bu bize ne verir? Bu localhost:8080 deyise biler yeni her hansisa bir saytin da url-i ola biler meselen bir dene
de enviroment duzeldirik local kimi (add etmek ucun gerek no enviromente qaytarasan helelik). Demeli bunun adini dev
yazirig , variable yerine yene -  student-host, amma initial value yerine - http://api-dev.company.com yazdig fso save
etdik. Devden bir denesini duzeldib test deyirsen, bir denesini de prob.Bu ne demekdir is yerlerinde 3 muhit olur: dev,
test, prob. Dev o demekdir ki sen bu deqiqe kod yazirsan onun uzerinde is gedir, test- isini bitirmisen teste hazirdir,
prob- test bitib artig production-a hazirdir yeni insanlar artig istifade ede biler. Adeten url-leri bele qururlar
api-dev developmenti ifade edir, api-test  test muhitini ifade edir, sadece api ise production-i ifade edir. Ve biz
enviromentleri ona gore qururug ki istediyimiz vaxt enviromenti deyisib he deye bilesiniz ki bu deqiqe men localda
isleyirem kecim local yaxud devde isleyirem kecim dev-e ve s. O urllerde ne mena verir, siz axi proyektinizi artig
yukleyirsiz meselcun internete yuklenib bu zaman artig ona bir url verilir, o urllerde bayag dediyimiz kimi (api-dev,
api-test,api) olarag qosulur. Select-all da tezeden sendi basirig yene cavab geldi. select-by-id- burda da localhost
yerine {{student-host}} yazdig urlde ve send etdik cavab geldi. Indi bunu update edek
Bir nomreli sexsi goturduk ad soyadi boyukle yazmag isteyirik -
{
    "id": 1,
    "name": "SARKHAN",
    "surname": "RASULLU",
    "age": 28,
    "scholarship": 999.00,
    "school": {
        "id": 1,
        "name": "mekteb"
    },
    "teacherlist": [
        {
            "id": 1,
            "name": "Teacher 1"
        }
    ]
}
ve burda da urle student-host yazirig ve send basirig exception atir deyir ki put metodunu desteklemir baxirig update
metodun basindaki putun value-su yoxdu yeni student yazmamisig amma bunu teleb etmisik deye isteyir bu da duzgun deyil
cunki lazimsiz kod yazirsan qisasi clasin basina studentsi atirig @RequestMapping("/rest/students")
ve metodlarda ehtiyac qalmir (yeni metod basindaki studentsleri silirik).
postmana gelib yeniden send basirig null point exception verir StudentsRequestDto-da toStudentDto metodunda teachers
gotururuk amma burda teacherlist gonderirik ona gore bunu deyisib teachers edirik. send etdik duzeldi sagda 200 ok ve
success geldi. Gedib select de get elesek gorerik update olub amma surname null gelib niyese cox guman ki map edende
surname-i map elememisik ona gore amma update gedib ad boyukle gelib. Bezen response da (update de) success evezine
obyektin update olunmus formasi da gele biler buda rahatciligdir ki gedib selectde baxmirsan daha.
Indi istediyimiz statusu qaytarmaga baxag (bayag sagda gelen 200 ok kimi).
Tutalim delete edirsen eslinde delete prosesi eledir ki onu tapa bilmesen delete eleme. Onuncun delete metodunda
deyisiklikler edirik (StudentEndpointde) -
@DeleteMapping(value = "/{id}")
public ResponseEntity deleteById(@PathVariable Integer id){
        if(studentService.findById(id)==null){
             return ResponseEntity.notFound().build();
        }
        studentService.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
}
Metodun tipini ResponseEntity etdik gorunduyu kimi ve if sertinde tapmasa build ile ResponseEntity qurub qaytarir. Eks
halda delete cagiririg , delete edenden sonra geriye yene de bir seyler qaytaririg - HttpStatus-un okni qurub status
qaytaririg. Statusu 2 cur vere bilersen yeni bele yazmag - return ResponseEntity.status(HttpStatus.OK).build(); evzine
belede yaza bilersen -
return ResponseEntity.ok().build(); but metodda- ok() iceride ele bunu qaytarir-
status(HttpStatus.OK). Lap basimiz qarismasin deye qaytarag status la edek hemcinin ifdeki notfounda da status metodu
ile yazag yeni bele -
return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
Tutalim geriye delete elediyin obyekti qaytarmag isteyirsen bu zaman StudentService delete metodun tipini StudentDto
edirik ki geriye delete elediyi sexsi qaytarsin onuncun iceride id ni oturub findbyId metoduna ve bir deyisen
menimsedirik yeni bele -
public StudentDto deleteById(Integer id){
        StudentDto studentDto = findById(id);
        studentRepository.deleteById(id);
        return studentDto;
}
ve sonra gelirik StudentEndpointdeki deleteById metoduna studentservice-in cagirdigi deletebyid-ni atirig status body
metoduna buildi silirik day yeni bele-
@DeleteMapping(value = "/{id}")
 public ResponseEntity<StudentResponseDto> deleteById(@PathVariable Integer id){
        if(studentService.findById(id)==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(StudentResponseDto.instance(studentService.deleteById(id)));
}
bu metod neyneyir qisacasi -not founda olarag body qaytarmir sadece status qaytarir ancag ok olanda  delete elediyi
obyekti qaytarir. Ve metodun tipini generik edirik -ResponseEntity<StudentResponseDto> burda ResponseEntity verir
statusu, StudentResponseDto bu da verir body-sini. deletebyid geriye studentdto qaytarirdi(studentservice de) bizede
Response lazim oldugu ucun StudentResponseDto instance metoduna yansidirig. Postmanda gelib students-delete-by-id
duzeldirik DELETE request novu secdik, url-ne {{student-host}}/rest/students/1 yazdig, send vurdug sildiyi obyekt geldi
fso. Yeniden send edirik bu zaman bir error bas verir normalda 404 not found vermelidi ancag error 500 verdi bunun da
sebebi -
findbyid ozu tapa bilmedi (StudentEndpointde deleteById-de ife dusende) ona gore ozu avtomatik error verdi. Aradan
qaldirmag ucun StudentService clasinda findById metodundaki getone silirik cunki tapa bilmeyende exception atir ve
metodun icinde deyisikler edirik -
public StudentDto findById(Integer id){
        Optional<StudentEntity> optional = studentRepository.findById(id);
        if(optional.isPresent()){
            return StudentDto.instance(optional.get());
        } else{
            return null;
        }
}
Bu etdiyimiz deyisiklikler nedir-  studentRepository.findById(id) bu metod geriye Optional tipli generik interface
qaytarir, bu interface icinde ya data olur yada olmur, buna gore if de deyirsen ki optional-a isPresent yeni nese
tapmisanmi? obyekt varmi? tapibsa optional.get deyirsen tapmayibsa null qaytarirsan. Deyisikliklerde sonra yeniden appi
run edib postmanda yeniden send edirik ve istenilen gelir mesaj bos oldugu ucun 404 not found.
Diger metodlarda da bele seyler tetbiq etmek olar meselen add-de tutag ki (StudentEndpointde) success oldusa insert
etdiyin obyekti yaxudda id-sini qaytara bilersen, prinsipce success sozun qaytarmag evezine obyektin ozunu qaytarmag
daha mentiqlidir. Update edende de obyektin ozunu qaytara bilersen. Bundan sonrasi oz texeyyulunuze , isteyinize
baglidir, heqiqeten de apinin yazilmasi texeyyuldur niye cunki musteri sizin apini istifade edir ve sizde fikrilesirsiz
ki men nece yazsam daha gozel olar. Ferqlilikler olacag is heyatinda da url-de falan filan ancag neticede json gedir,
json gelir.
 */