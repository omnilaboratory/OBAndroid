package com.omni.wallet.ui.activity;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;

import com.google.common.io.BaseEncoding;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.utils.RefConstants;
import com.omni.wallet.utils.UriUtil;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import butterknife.OnClick;
import lnrpc.LightningOuterClass;
import obdmobile.Callback;
import obdmobile.Obdmobile;
import obdmobile.RecvStream;
import routerrpc.RouterOuterClass;

public class TestPayInvoiceActivity extends AppBaseActivity {
    private static final String TAG = TestPayInvoiceActivity.class.getSimpleName();
    long mAssetId;
    private static final int PAYMENT_HASH_BYTE_LENGTH = 32;
    String invoice1 = "lightning:obto2147485160:100m1pjr3628pp5v0aeyrxpd2pzdypku55ndk4c0ylvfzd6p0r60mv8tzq5lujmlqmqdqqcqzpgxqyz5vq3q8zqqqp0gsp5up86we5mcryjqsszr3ayvm254f4v0hw5w7k72tntm5euaafvxveq9qyyssq303evgux5xts4u73kqyye7hcxv63j9xngc5flsmq8uvuay5mh7mr9zjfenmmfd78uxj8mfmfqz8js0atluq76wcv5dj243v7k8m34qsp9v7x0z";
    String invoice2 = "lightning:obto2147485160:100m1pjr36trpp5v7yju4tqh42zszx8nnvdg80wm84uquvlhugekt676f4yv04zdypsdqqcqzpgxqyz5vq3q8zqqqp0gsp55fnn3vs8d98w64aqudsurgy9jdha49duhdmzv5tm008xdwwm73wq9qyyssqxtn8judxuxq3u060sx7a2cthuzmgxgwm67r4ye7nv6upftfl4ku97qmf6pecnc2rh6magw85g80tvfmayxrd90v8c5vzupdwwagvjqgpewppzv";
    String invoice3 = "lightning:obto2147485160:100m1pjr36t3pp5k7nzjew0gr7um2mqtw03tk7qfkajxm6068xnc7tvrtrxekezvn4sdqqcqzpgxqyz5vq3q8zqqqp0gsp5fvpvhk5hvlt7gvt8llq28q56m5ch3dwk2qfu7cyssqz87f9l4hvs9qyyssq4zxyfm3z3tawdzpm8pjsdysmlyz5awyx5sjr4xt4t6q0hmez2c3jyxluvgtg8f0j25asamz6wx54jpf8cuhyjnfswy3aqwqs6lgj8lsq8xy4dn";
    String invoice4 = "lightning:obto2147485160:100m1pjr36t7pp536l32w8fm7algqdnkuyq745ckqx8f3fkqlyxax5085n65zzcn6tqdqqcqzpgxqyz5vq3q8zqqqp0gsp5h4lxzzsvhcfa0qgmpygjwmc4hl8yug0ssjvum967wu2ccqwapwqs9qyyssqhrdreyyrwhd0uj6dctzng98g34jq4fmg5870z3uj6l68prrty0ms626n6lnplftnu2vda2ah3vknw5s750uftz5wpsucjyre77vmklcqpdfw53";
    String invoice5 = "lightning:obto2147485160:100m1pjr36v5pp5yr54zd7edxz5ypp0cq0caf8kpeqrshe9fpaj4d79tw3y9s3v062qdqqcqzpgxqyz5vq3q8zqqqp0gsp5h7a8wtjnt53thdzt7ej2vuxy0lltslzhsjm8gxmtz4ctmw8g3w3q9qyyssq839tzdpe8r69hkvuzekeepd9t7rhtx6ydk5pqgenu6qq72gahhz494lnxgqkg3xjzyku07mmd7qw92xx87n3nzymfzl0c8g5l0nv3tqptyjqwp";
    String invoice6 = "lightning:obto2147485160:100m1pjr36dppp5new6rr8fwh2hmzpvuyz2n0atws6kd2sahcwg9a5gcx7tnsucfdssdqqcqzpgxqyz5vq3q8zqqqp0gsp5w27666rpwt2w3pf26wz96g2hxmufq9uta6ftjs0n3eml4yqjcg6s9qyyssqjejlyefq0uz6d2cdm87uu9fexvhjzsxeg3mylku3kp79jx0ajfr9uve6j2ur68r6pevz3qc5724em3aadswlctxrjxajcudcsxtzkcqquuedhe";
    String invoice7 = "lightning:obto2147485160:100m1pjr36dwpp50s6wpnsaf2tlkp36zk2cnkkqm3ev2vvmdumjvkftddcdr32wwltqdqqcqzpgxqyz5vq3q8zqqqp0gsp5l4rxeg9t3aewge5f9eelyc2trg9x8zdu5xs4uf3tuplg8r8cf6xs9qyyssqprw75n8l5gzwfwmrfwtzyrg95vj0u7avgnvu2696lhur2afnrexymel8tqrwp2r47memjk0z9v3uwyhg4mz54qzrzca324cy7vwtt4gq7djp0w";
    String invoice8 = "lightning:obto2147485160:100m1pjr36dupp5c8m04nzxtrslvuvmtk9g2jfdeaes87tw67lfuljgr3ncgwrkmn0sdqqcqzpgxqyz5vq3q8zqqqp0gsp5spxjc88h5vzg0lxgfsynqle3pk826mx73uczq5za378a30pheaeq9qyyssq8rcacfjrn3dk9f4q0usj0asc3f6w22d43zzrr5t49ln64uesp0dr4yvmn8dzt55pcy60helrrrhllnlmwmk2hu3u6h2uh2y75xxuy2sphyq3sd";
    String invoice9 = "lightning:obto2147485160:100m1pjr36wfpp5sgpakjlpkwwpueae3y2v2shcwhm8dtmvqmnyz3whmnm3rygxt23qdqqcqzpgxqyz5vq3q8zqqqp0gsp5na06v6qky4fy4p0tqsressds5ezsgyfllp4fpe6n3shckl3lgsrq9qyyssq64hzheaqkkgjpqkpfulxgs3ecd07azxclfpucxwhnkyw9lpzexrrchf4fzyqkf7ssj3nepwu9yrpwetnj8fz0f4twujqj9f05k4rnscprcvszj";
    String invoice10 = "lightning:obto2147485160:100m1pjr36wnpp549vjpkevx07ndjm5lk40uaujz6mn36gsl5xj934rm2uurl89zxhqdqqcqzpgxqyz5vq3q8zqqqp0gsp5wkzvslzdj6yvkwxat56l89vvxnu5u6cj3t5e5h8k8zu4hvcgzkmq9qyyssqpzkp3599z633txhlrqnhvrhdpuqa0w4458hcdr588hdn53qf0uhqvg33dh8fk42kd5xps7dnqkdn0tdutv602q4zkclm7k4jx6fjglgq9qf6qf";
    String invoice11 = "lightning:obto2147485160:100m1pjr360ppp59gxme8g6wmjzgk580qdawugln3gd9krfm4xpl6g477y9efrtzdesdqqcqzpgxqyz5vq3q8zqqqp0gsp59c4h8u49l3jsqcq9p6p302zuqw6ehyushr4nmp3l0tvwje576uaq9qyyssq37rf5l64pyj2rhv5uq27mu8u3z4kl0y9qfe4hax8hdsxnsfdedypnpwqunf24lj9f3z8zwtunfhxds8fjpg9mmtxsdyvy9k64k4679gqzwkf9t";
    String invoice12 = "lightning:obto2147485160:100m1pjr360dpp5afrr2satasrv4l6f2qt2pgae9fvsuvmdlmhqgdxy0vwzcqu42ktsdqqcqzpgxqyz5vq3q8zqqqp0gsp5vgkmkave3rqaygcnh3dkp02wc3ru2wy9jzsjsf7rg5cx3tsxk27s9qyyssqckyzg95ewymwxnrdc66vmwdedgrejy0jd08kd2kq423xqvfwz6xjypucuval8zye9kggtst0gh73ydqg34uwaagxzn69ds6rncj6lmsqyl879w";
    String invoice13 = "lightning:obto2147485160:100m1pjr360cpp5euxturdnaypfq055ads9xl50j2k5mgak48cv4pvet4epm02w6r2qdqqcqzpgxqyz5vq3q8zqqqp0gsp5e9a09zr562djucpre0s6qtkv9lgazmucd03h55pljgezy3yqy4eq9qyyssqm345msf90qr7h647k6c6ulm8c4m8rg96yctpk2lqzg2jltycf8q88jeg4a7g6xw2axrs4uypr4dvufm44j80mmykjzye4xcz4yvaptsprq9clz";
    String invoice14 = "lightning:obto2147485160:100m1pjr36sypp5w2pelluvn9lxmt26z6ntjzv5u0ljk2sykpzldaw2kru2j9fcjmfsdqqcqzpgxqyz5vq3q8zqqqp0gsp5v4zcnxve69nnhl8rrej4sj8rg9hjltcml9jddtq6prh8hdpkacds9qyyssq4tctqkc0snpx08f5cgut60cwwl9pmg4xlfye2af2rahrgz8amttjs7jmqc5fdkz95d3x7t248suz6q4v90pyvtp5nvqx6y79e2shyxcpkmtkqc";
    String invoice15 = "lightning:obto2147485160:100m1pjr36s3pp5twcxdnfkg879ttkrmkkcvrq305lceyavpwzpdpfsnsq56clr9a3sdqqcqzpgxqyz5vq3q8zqqqp0gsp55xs480y7sxchwxw54jxxhx2s26c3namag8tvpd5gqgm8y842wckq9qyyssqgf9ymrzhywspfz95ewnv4mrc4qxtr2py528u84x7zvrmvaa7qeg3cvkhe7ku9p2hrrfu439r5g9hp8rmkyulxynkzjyrqx4e0xya7yqp73m0du";
    String invoice16 = "lightning:obto2147485160:100m1pjr36s7pp5dndema2hxc8hfj92724665s6ujpj2nd5uy727edtqlnayapqnkksdqqcqzpgxqyz5vq3q8zqqqp0gsp5wfvpuqn6fes7fff3pl6ysg3v6n46plux9qfvv8yqatp5uuc7sgrq9qyyssqzpkpr9dvrvc8jmkzef0tx7znvkvjyt6rydhcfhfn7nly0g6j730phxuf5fnknw5pl64sue6xjh8d0nqfdjmx7d06fhr2m4jmc35fpzsqrxqa74";
    String invoice17 = "lightning:obto2147485160:100m1pjr363fpp5hs950ecmsyhaupwwp72ly232zh7ecpljhen0mzsfa8ufat02ugaqdqqcqzpgxqyz5vq3q8zqqqp0gsp5y7y770xt0f6v7m5er2lykak3uq6zjqjas62u4wgsmeqk8lxe5wzq9qyyssqvxy2cry2uylpaqcygfgc0nmafe6lvxsh5gezq0dyt3s4zmk4va2sxjy56z28swshc585vpwu94nu7dp784eztwaqr43vcxhpck4r58cpncxy6c";
    String invoice18 = "lightning:obto2147485160:100m1pjr3634pp5yqqh3v87rezz0387xpy5xwe8rwjv59h69w6q4um35try6534thhsdqqcqzpgxqyz5vq3q8zqqqp0gsp5a4chqw2xs2n9wnhhd5ufkun9re703c05j8v9zh9ch64ryyp7s34s9qyyssqauaunqxjkwf5mc0qdd8j8ks5ywrp4mpj78ajc792s8ylqveszkap2fej9a67lk55u8sqxzap0j65ymlletssssfmu2m3nzms2jr9nhcpcets6l";
    String invoice19 = "lightning:obto2147485160:100m1pjr36jrpp5hwv4xr4lqmltvszm0mzge00sjf8mw5eall5zj4ll2sw9hvprn54qdqqcqzpgxqyz5vq3q8zqqqp0gsp527sw5khlxwl7m06j0c8cfyw22pmzmge0qm95gus0w32gtal6x38s9qyyssqdkl6ze5lxdqnurahjtljqguc4rlzxl4w42qamhd7ztt865sk56r973qraapxdfdc4fhmgz782pyzucv3p5s5lcxqw4erecx768ledkqqp5qhwq";
    String invoice20 = "lightning:obto2147485160:100m1pjr36jwpp50jqnh2cxmh07jzehxya9degyt8gn2ajs2uqgt3nnr6l2a75f0xgsdqqcqzpgxqyz5vq3q8zqqqp0gsp5x4xhevnggnpwa9v088ee6czkd34npa9vt6yw7ndgynx4g9swhurs9qyyssqcmttcerrkuv8l2xk4ql2kvwn36zynr5hl5aq7qdw0vfut8gnjk996nnx5kh5cwhdtuk97m066tc4khz0w6fn5erdcfx3u6gpw7hu3nsp2h4j90";
    String invoice21 = "lightning:obto2147485160:100m1pjr36mqpp5mnt7cnpp58ddqylvr8lpax8cln30mp5zals23pfwzqv5wa0p552qdqqcqzpgxqyz5vq3q8zqqqp0gsp5kmu99erhnw0tcnj9psd3h28r63k57ky8rmlwrmz4xsffp36qf00q9qyyssq25qkcn6cv05wl4jkshhkd80xpa7qc3v9du5p222a9fccgy2ry2q9gs695g6683uy7usz64ulws6x8cjmhrgg4tw6g6jz6h3ptlx560sqfwgy30";
    String invoice22 = "lightning:obto2147485160:100m1pjr36m7pp5dc6n00l3zwtppx8nfgx0q86t3639ej8dfwf8nfc56fscl53j4ueqdqqcqzpgxqyz5vq3q8zqqqp0gsp5negehyuxpa0sws4xr9720zg4qp4ahqcvtjr92nll4lk7y42d8ahs9qyyssqmlu4a3q0k93juamc0z8su745tg6aajtckd53yequ2et73mpnzcg4gvg0ktyy7eu8x4w8jz4nlpzfapds9x68ejea686ra2qw7smacdcqfadvz6";
    String invoice23 = "lightning:obto2147485160:100m1pjr36axpp5edswedeh3pxfqpq5s5np4ux6zxhkyy93vyas2syq7tr94vwur77sdqqcqzpgxqyz5vq3q8zqqqp0gsp5qcxeds2llrk7t3xuzy7f5vdnkuz5eqrm82xnarjqmcj5jmrc73ws9qyyssqlu83w2qhqfswvrmv4jv5emsn5pk4n2j50c6ut8q84dr93qjle5nn2xdxc3za48alk7lvlhvnhgcrd2mqemvuf3esln6tywmjze4353sp7ckgw8";
    String invoice24 = "lightning:obto2147485160:100m1pjr36aepp56c72egqjlmlj4psy77lune6cpzqv7stm3jwzlvqfdzp23ppg77ksdqqcqzpgxqyz5vq3q8zqqqp0gsp5glt6whe6hkfq0zsv4s0y7tu3c45p0qtmfk0pfj3ya9k5sfgl2gms9qyyssqtaqakz09aztjd30tg5l6tlrcu8rg0q6nkf25wm7x029y7rlrvxy3tja9j6jthp6fdd894m585ht5aqyxsnp7ye7qmueyjva0auhgy8qpk2ntzk";
    String invoice25 = "lightning:obto2147485160:100m1pjr3679pp5e3eyenhxpfxultcs0ejpwmfhfnzqn4e032ys7jesxsxnupcdg2cqdqqcqzpgxqyz5vq3q8zqqqp0gsp53asj50lu50wz6xsmjdlle7y2m4ax6psfdfkd0er363c6vs62jq6s9qyyssq2vd26flv69vskjxzxwh2jpwqucdaxs365cudzxltwfq9520kfv2qrlcf3hvcwx66ava48v6x9d3ause93z2t07qrjnf84uex6as8r7qpqakfvg";
    String invoice26 = "lightning:obto2147485160:100m1pjr367jpp555q626qp720gkdlzwm6hzw4q802ghvchgf57gm52ufpy96cjrt5qdqqcqzpgxqyz5vq3q8zqqqp0gsp5jp3mpga2ms08d6qreeg9mg8q63eurw9nuyzzwxelerrehsjgtkns9qyyssquktzst0yqmuvd0k837pc5n9g2v9eukvv3fyyajp2krvthzkuwglkwnkyx2p0ly9wmcuzs7ceqhl9ksdnnyj8emznctcna4psz0h6ffqqgqn4pj";
    String invoice27 = "lightning:obto2147485160:100m1pjr3677pp50fx7aqfmrwwjzgz65lwzvxz87p9y5ms5v388jn2sn30ntjxah64qdqqcqzpgxqyz5vq3q8zqqqp0gsp5yzzw4v0cw6adycxx8evr3ltd43azzvwhqq2t9vffac97w7wk6arq9qyyssqs9m29x9jj5fkq4vyvjuqkdye5ke4j26pt955ktt8vn03h0jnznn9l7ukrmw256kedawjxmp4pdlzcjk8saey784u8438zgx6epfx2vsqgrj0c7";
    String invoice28 = "lightning:obto2147485160:100m1pjr36lgpp5s2xq4pumtgda5pnkan7shd0dpzq4xhaztmtj0mqecet03k0m5xxqdqqcqzpgxqyz5vq3q8zqqqp0gsp5fwd74pufxvq44gj9q8hrnpdm9t3w89h9kzhxe889twlys8taqfzs9qyyssqc43nlrp0kjrepuvg53qe98nhh2kgq0f5m5uvjuj97px9dn56u6q3f43aymfncd8ss7k6cxycs0w5z9d0vwkf0ng243sshmg0dvg93jsqkmtrwg";
    String invoice29 = "lightning:obto2147485160:100m1pjr36l4pp58576nwer5kge77ct7kkkg5h4d4pdvp04t7xaxpfkjd33jfn06whqdqqcqzpgxqyz5vq3q8zqqqp0gsp5ddfy0ut3yykn5mrnaemhx3qm64wdllfcjpydgf2egxrssxwyadtq9qyyssqgd3yth8qn2u4ga4mfmq2waxcqn2jt2ux4lghk8wc22mgknpc0emrmr0adfdtcg9gyf7378fgpwmudpk8y2qd5dgljr20v4kr04lz66gqyptdgm";
    String invoice30 = "lightning:obto2147485160:100m1pjr36llpp5yu0vahyz2ju345kldmevghau42q7s006a46g58nwjkhezfzwphvsdqqcqzpgxqyz5vq3q8zqqqp0gsp5a7gvqcy77znwk6rpw9a9sjj5vtzfyplqkr2ha60tyn3kl25phc0q9qyyssqsdqaxsehph6gxsl93gdlx2wdralapf5mfurne640f8r2mzpjw5aqxfazlda6apyp67mnh7nhj7vt72l3hf8uvsnync5lgvnln2rclzqpxyh0g0";
    String invoice31 = "lightning:obto2147485160:100m1pjr3mqmpp5pnu0t9fneq9vd2yfe90ws6q4khna07z87sj4d9mnylcmwun999hsdqqcqzpgxqyz5vq3q8zqqqp0gsp5eelhe9qq0ts6pr7tv3yy8xc53jjh6ke88yuzs49z9e3ln6kr466s9qyyssqcrshpajxgt8ytfj0endty929jq72vgg0w8scxflf5rn3zwtxl7452ym594zwefe72kr8uwcvx8nqp89wtu9vvzxkd45fkhsps0z9j6sp7paftu";
    String invoice32 = "lightning:obto2147485160:100m1pjr3mp9pp5cu0q5tle3stp7a3lqanzz6adj8pczam6vu2gakgu4x0jj9kjlw4sdqqcqzpgxqyz5vq3q8zqqqp0gsp55r4r78fymecsh864fqclqfu790wzaetdudphee9za5z479r3y0tq9qyyssq9gv8vas04gyzf8y6zezvnfka7vg3ynsv099qhckptkduq3dkjj5s76hchgv47qjctpy7lurht4j9tlydj35r8029jl570fyt9awye2cp8uur27";
    String invoice33 = "lightning:obto2147485160:100m1pjr3mp3pp59r3jr2nhewegd0xlf3p3eptav9w835zl5dcfucv7azcheg4s808sdqqcqzpgxqyz5vq3q8zqqqp0gsp5r06x9a30p9pvwcuqj0587er4ddvshlvumlgsd9zuwq3ljted702q9qyyssqf0ew29eg7scxeu6k4hlu5649v8g30espy59e9em2h78m6e2vh2rswn6z03vf2awxt6dmnruuet9c76v42fq0j6jh6f7qu0g2fe4wpsqpcdjh8g";
    String invoice34 = "lightning:obto2147485160:100m1pjr3mzppp5qq4ulmcuc9arkdkv89auhz86ethafleakkcr0cne8l69cx9s7xvqdqqcqzpgxqyz5vq3q8zqqqp0gsp5r2g0fwp8ef5zev75vlav2egljl5gm4m90mz2pfwz4zt8ef9j4hns9qyyssq72l45qvape5h6dclw9fx8yw9d4pddwqryerde9vw94kfgp7g8mdpfu7hedhyhgwtwv0n7304qnjh8uy80gg480g9mwvs9u8hvx06gkgq22snzw";
    String invoice35 = "lightning:obto2147485160:100m1pjr3mzvpp5fdgx4ncpgyzj20w7cqehqnet696lvga8dve779s0uwgd2uwjesuqdqqcqzpgxqyz5vq3q8zqqqp0gsp5kpc8dywdtj2sh00tpyccsjhfwe4af5n97crv70fu2345th77k6mq9qyyssqcxd362jtx2w0znpd6fnprgjl3antah3yrkfu9d69rvqz2yjx7et8rz0p5a3qzspt0jhr684s4tf3nmua6v9f9s45x6vwyqzup90t85cqxtp9uw";
    String invoice36 = "lightning:obto2147485160:100m1pjr3mzkpp5kf0qtuvklkugfajy6ahcsasu3r5crhht2chth0rmc5ls3nlapgtsdqqcqzpgxqyz5vq3q8zqqqp0gsp5uuwl650852s47uzq4qrq8xh728vyjkt9nhk6p9vnsuxt6e4gpprs9qyyssqscy9w3y94dmh33ql67gtj4emllyzwp3rmwjt2ltzj8jyumcspcy3jhan7eh5fkwyzemtxsgfz9y660asqc9cnfnpr6r79hnhvhdwzycpl4edk0";
    String invoice37 = "lightning:obto2147485160:100m1pjr3mrrpp5cw5crm5dcxlwrffwgkw7e759us29ensqy2qv8c39zsh07gc3ttuqdqqcqzpgxqyz5vq3q8zqqqp0gsp5qzvr0w7mjgx3mctcuzmzyxc87yzrw4t9vyyjexn95paurpvvq73q9qyyssqtd8n4nzcd7t7lw0gaur2pa0emma20enu4edv9yrm934vt5mw9ads0ne024pex7j8u3jkkqj6q4up2qgqu3l7gpuxdjkwa2eu3cyj6xspte36q6";
    String invoice38 = "lightning:obto2147485160:100m1pjr3mr0pp5524cdtsp2gleqkmvaxsu4r2hgddxdkamxlxkuj5cxnvcjtj4xezsdqqcqzpgxqyz5vq3q8zqqqp0gsp5yeafkeetgr57nrn5fhthnsrju4lrpfj5ptnlyhcmhqjpyqcjpdnq9qyyssqtgs8lf9fz755cjwlscekyx247zj5pw8khycwzeqm7cyccel8nwyszvda3fr2nmejnpykyut0sklnjvvfq05rdrk4v3c76t5wx9e0p3cqh9a5au";
    String invoice39 = "lightning:obto2147485160:100m1pjr3mr6pp5h6l9exc44j6q2rj5n2f0fa7k6s8wu67kwu6uxs49ztlulp3kvfkqdqqcqzpgxqyz5vq3q8zqqqp0gsp5zr622ljzykj2nyhr9rqc66ypkjmzv7s3f9puvjdpm5uq7vegltas9qyyssq28esrqqzc75p4y34srww0a47875k72pgqlhphuku7pw6cpgn6cl94klceykhc6xha60m8ax5qlnzcr45phdmu3czpqjc3pd5laauq2cpxgcs2x";
    String invoice40 = "lightning:obto2147485160:100m1pjr3mygpp5zutyq6d8up7z5rdmjpvda55hezy0psht987jz3nesvxhhne4h7xsdqqcqzpgxqyz5vq3q8zqqqp0gsp569jhtguczn9vae3d4wnkkz0dqk7mdh0g69y3zed63zke07z8nuuq9qyyssq835zg4gdvgtkav6l8xd6dk394djh6lf76y5y2rz0ftchkffw8pakgng9klqxhhcnrc8ax24d5y2e0n8nr33j8swxrd6ct9h9zvtz4fgpd8n2zv";
    String invoice41 = "lightning:obto2147485160:100m1pjr3mynpp5u9cd775dqwjw2r7ffnghqn2rmtwz6te4xpe78nnw9rx0aw744acqdqqcqzpgxqyz5vq3q8zqqqp0gsp5jldd8m32zyetumff7kw2ppk3tt5lfnwj5raxxtzev4js3qmrd6sq9qyyssq7eyqvp5csd59xqthcken65qruulpllzxrkdfwtlpux9h2vknf8t4nc6g424egddn8lxqlcszrge9pp30e3pajh9t79zlr7dndq2643sph5l8ma";
    String invoice42 = "lightning:obto2147485160:100m1pjr3my7pp553y508mumjlgeywgt252yucnd3v6dj8nfmsdnsexc568k9dap58qdqqcqzpgxqyz5vq3q8zqqqp0gsp5gg5kdzzc3a909xx076694heywuudl7qyjwtat0qhnpfrzxac6h7q9qyyssq3vgj5vt8cghz7ywvw9t6z8vwtzldqhr57fjvrcyhajvnu5rfj57qm0kzh0ex39ulcvyjdlqvcf5d3fcz5dagqcs86x8mcuuz6ut8gkqq2re9cg";
    String invoice43 = "lightning:obto2147485160:100m1pjr3m9tpp55qq808enws4ekl4yc52e2nwm04q96cn6fgjxkwjx5wwvm5htfnysdqqcqzpgxqyz5vq3q8zqqqp0gsp5gal8p4jmstnklxvac3fym4p7qsyghyvc0q7j7pemg2vuqexe7wpq9qyyssqlv6zxn3wxsnkdlh5emdfud8peu6kes4kdm5e744chzcfxa4eycujrq5zj4t0xwuku0hfs5atrvs6twfmqp2ftz7rttsdnurtusnahwsqr7v39x";
    String invoice44 = "lightning:obto2147485160:100m1pjr3m9cpp59t6twasrzl4hharx9hstd8ar4z387wh8qaqqxwxa0uhv5akag2dsdqqcqzpgxqyz5vq3q8zqqqp0gsp5qzr4c4mgelqd2t33q9320rt03ax2u892x0wszujpsqzpkk3872as9qyyssqj9acghw8r9levv7mqh7k6s29858rh04tn8mdsz9h8vy0fh9jt9d5skfx2mqpz0vnqwadae6kjsmpcdted8w3t4jegs4u539juqqztkcq9ye80h";
    String invoice45 = "lightning:obto2147485160:100m1pjr3mxzpp56zznxxe82h5cg325sqmjvjkt5vvrc8ac9r8d96rhjg4upeckdj5qdqqcqzpgxqyz5vq3q8zqqqp0gsp5vtqdut4c40f7nqh657wejjn3lfm067lfqan4cqf6vqk4y3lur34q9qyyssqpqlyea7jkxu3lqn526y7h9vw2vcrgxwgmwyry6huxvx4zwe26nzru987y8zxp96fqkvlfx39ugn549j9zt45qgw20kspjqa3h74t6zgq5l2pkj";
    String invoice46 = "lightning:obto2147485160:100m1pjr3mxtpp5k58sucrnmzxll2y0wmnjmpvyszz0ykhrtltlj723r3quftxpvvsqdqqcqzpgxqyz5vq3q8zqqqp0gsp58v737pwfdjfcfpu5nyhwg42d5yv92t6usylgz90ewsmmjjt79k4q9qyyssqn85apznjs8q5jwd68zhjcqaj9p7np24s95zll66ra5752g79wwnpvq3z7akvl2l7kkf85z98v4nh23plgmlzsz3frsaz9xtd3nzvegsq94nmme";
    String invoice47 = "lightning:obto2147485160:100m1pjr3mxkpp5694fwrg7mnrcaz0np03n9qlzf8hd93h8krt2ug6whh4wq82vgclsdqqcqzpgxqyz5vq3q8zqqqp0gsp5mur560n94pcr5xasyrl3rslpyl6jgf3npf55lf2zx46ettfjxe5q9qyyssqlrw2h3acrspss92nx4qcmsekzd3a4qmp2ueyylleavnv0240z353lfg7ggjxfsk333uezykuygmhydw3gaynn4y65fz92azlgw7jkqgqcmxvza";
    String invoice48 = "lightning:obto2147485160:100m1pjr3m8qpp58mcpedpf753pc9l0m85vlhshk0gcksxz3hvkh9cwstda6pauklnsdqqcqzpgxqyz5vq3q8zqqqp0gsp535gpal4cqjrav20v25xha9z67yqtvg372hdta3pxjhvwt56zjc9s9qyyssq9jk0xtzyljxhs358vltdemshekwlmwjvv7tg0nml7v8dyfwvt6j3r2t35j9x4utxf9ejau9grz9rvghmkut5vs205fcznp285tv8pkcq38unv6";
    String invoice49 = "lightning:obto2147485160:100m1pjr3m82pp5ft2xfct0e54g2j79kk3w9lfkyzy37e4z00v8pcq2cahu3s7t86gsdqqcqzpgxqyz5vq3q8zqqqp0gsp5m5zw2msq2vukzqh8f7stkxjara77qxd4l3zwqt3y5ecnz4jtvzmq9qyyssqzcsxpmg4zjekr9asghymj5c5vh5et9tzx4egxn6pzuaszxfqakvqv54qc8fcp4fhqnthstfeyccpyg9f20td3zk0qunrlznfms7achcpyga08r";
    String invoice50 = "lightning:obto2147485160:100m1pjr3m85pp5yuekt0krfdpagu4qlsq0nzevytcpx2dv90kj3yr84jvhvt7nfezqdqqcqzpgxqyz5vq3q8zqqqp0gsp58xr7lt8yy0m5qvjlc2fnkjw4guhsm9sd8tzdpxa48qljnrv6mtaq9qyyssqxjrtujy8mk9wn488ax3n4xtmq6tfkw63w6x68tr46curc84tkywrqyu8ag7ldw4aj4tp902g69f97v7qascc26dc75qy2snkzcqd5dgpvwuw26";
    String invoice51 = "lightning:obto2147485160:100m1pjr3mmwpp5my8zdsvmh9m97e6sepuudlpaxwc2qapazgk0wx5l0uxsnylzr57sdqqcqzpgxqyz5vq3q8zqqqp0gsp5wyas0lrsww4emkfj0rjuy4kq3yawxal3a9ca3jg7vljr6rx04y2s9qyyssq892xrw4kn0s4td95u3jr3tud3d7uqtav3ksv4qtuvt6uhmfy3e94nu0kexa77yynr3gmu8enhlr40yt7574ecv96xzdd72mw0wkug9gqmypd98";
    String invoice52 = "lightning:obto2147485160:100m1pjr3mm6pp5qc8vt2w9vsqetqwm07wns67el7u2p7lvc32cl68wjjmuthfesf5sdqqcqzpgxqyz5vq3q8zqqqp0gsp5l49cv66h4tk40ha6urh5k6ch7pcs6w6aqsjqf9a2fes3zvasl3js9qyyssqg07d5ast3r90n6f3th7eg0knyua5yvfhjrtey5hnh5hykax7uxa8xh2uq30kpcyq3k6fqme83m9j5zf5eyj32n0yx3c9gwmsvc9xqqsqgwgnh7";
    String invoice53 = "lightning:obto2147485160:100m1pjr3mu9pp5z0ktj7pzcu4v47x85843g80c9zzwj59y7hheze4jfcsemejx5n4qdqqcqzpgxqyz5vq3q8zqqqp0gsp5s847ws5pehpncdt9ta28gq90aplldvtl79zhle9nlck0drzz6qvs9qyyssqq32uldem9phh0xuvwxn6w6nwac8fucet7edwpju9u966ar6kz3erzw4njhtmvrlvmh30t3jd7arg8kjmzupx8j9t423xge826knz4kcq73vq4m";
    String invoice54 = "lightning:obto2147485160:100m1pjr3mujpp5dfmvx95curau9ezxdxjfaxprhnpuejm2lkyfnrys4fqmev597lhqdqqcqzpgxqyz5vq3q8zqqqp0gsp5avf6d3gd9wrlwew4g9f20urvm7mp92ahuvykua6gmu8dseeepkks9qyyssqx5mtxwfm77avhqwednfexswh9sm5xfymz5ws6f37npfsvmg4u208mmgu2qaarh6xlulyngz7mjjwxc3egrxa9acq9pgsud747ywshscqmaz5uu";
    String invoice55 = "lightning:obto2147485160:100m1pjr3muupp5pas40jrqn5827wzaxlv4ce4qhyws9n278emfhcjsahp52ttdmrpsdqqcqzpgxqyz5vq3q8zqqqp0gsp5hvy3qfcmz6xkktyz5klgw0cfxe0c72r336qmtv4f0x6545vnvscq9qyyssq3rqpp45s8sup0jpsnun53gu82k75283fu8a06fmtf9le48mtj7jxrkh0cqd58d8rm5862lm2474w8mmc0jguzdzta0tq5m3nn9ff0nqqzdexg0";
    String invoice56 = "lightning:obto2147485160:100m1pjr3maxpp59py2cwus74mk6nzjuygmmmhk4w8h4khvcq4hxxpfv7ny8jrv6jmqdqqcqzpgxqyz5vq3q8zqqqp0gsp54djx8zvzme8hx85e7y7k2gczcfgxf7nxx0x0f6f7kf0rscs9n3es9qyyssqty8hftyrh7f9n493nxmuzw6w3zcj3000fgp26cwpxh305dc0v09pf09l8cayyqqqmys2syyedddzyzvfj5lrle96t8up0kurcr8cwpcpmgtqxj";
    String invoice57 = "lightning:obto2147485160:100m1pjr3ma5pp5pvcrkxvd60c7cqk5g8tnldg9wa3xnchel57a8cmyv6st2kk38keqdqqcqzpgxqyz5vq3q8zqqqp0gsp5zua34f6zmngp4k66s2ps46lz2k07gqjhnesp0llccdar00svpqxq9qyyssqa424mv2hupt0vlgr9d6rufvurss6q00psyf7e64ljg9jvjeklvkkrnht8x6aqzjcytulqq8tdg5xxt8j5ecdt07hdgauhp3s98lznuqphzrqtj";
    String invoice58 = "lightning:obto2147485160:100m1pjr3ma7pp5ruts4seg2z2t33wy0ujsxl36hew42637lv7y6xtvg5la5dqr0vhqdqqcqzpgxqyz5vq3q8zqqqp0gsp549k72rd8ghval27j8w3mlsae2durqq78wy30mgkhkwtse0lxlp2q9qyyssqylfe2jt6dczh56v46ccvan7q8y99fphqa9kcxcvxvap3n9eazc8zhad2q4sk8h6l4422u5vpzmcp86pdgc83z8jewntljp08jr6dcvgpn6j37t";
    String invoice59 = "lightning:obto2147485160:100m1pjr3m7fpp5gqdawchvs5wy4jmdl0p92xkn8gcf4hmehejghuwxgmcs2md9x64qdqqcqzpgxqyz5vq3q8zqqqp0gsp5q5gxjn5da4475kk7gau275r3uz6gk68dsh2ae9ddd7fevqqm2wtq9qyyssquvcve5w9d6p8e8krj57wrpeuwuyy5zxnhgettkn9a6hw2w6xej0hdfqg9hmvklcettla26j3dakhw95fth23jwfhcpe8hf4m7fktnggq9zq96t";
    String invoice60 = "lightning:obto2147485160:100m1pjr3m7npp5my8jr56ey9jzn57xeaznrcryztd5373s038nqrew4ajx5rk8ezlsdqqcqzpgxqyz5vq3q8zqqqp0gsp5xtew4paqc6xs3ud7kyzxfta596ljterakhsef22pf4tjjhqvlzws9qyyssquuwyxhsm5qy2mtn3rf4dxwycgakvlm3p6g7q9d8eqdmvmtfyuxdkv9489pf9kqap42a5k6gvkv5nmpycjttnuxgx3nx9685v5hzu0ggqfsg7uq";
    String invoice61 = "lightning:obto2147485160:100m1pjr3m7lpp5p0lxmk850ttrlw4kjuv9mw4mjukl9ac0l2knmf6e2j0hmq5au22qdqqcqzpgxqyz5vq3q8zqqqp0gsp5cr7tdfc8ta23mvmannj2kgcpfmfm0nu7rzctgnu5wh3mqzy5pssq9qyyssqn5l4vx60atp5q6huewup5yuaeg8fwvtrngh2p828r3s3dkhch20xm2qkkaatdkaajev5m6nl8uasa4y96m7mz00zfxl64vfyeg97zlqqyaettt";
    String invoice62 = "lightning:obto2147485160:100m1pjr3mlgpp5z5m43tqqderj8auswt2kjjqpzupxn8qaj9h3nkxfynm3646tnftqdqqcqzpgxqyz5vq3q8zqqqp0gsp5jqf0r68gr9amqd7tgrszk9vaczchg23w3ld24x2r365pu3767lus9qyyssq8pjs9zv8k6n5fts50gact7aw6gvcpavpcxjwpw37pcmwuj3ftn4yc0wxqekp0jk27da0pnplf4l7y8lhqwk4qdlee6273hn8gg07fkspgumel8";
    String invoice63 = "lightning:obto2147485160:100m1pjr3mljpp593sr7cxsaz5hlfv0k0hgpne4hxaac2dtac7uxdtw0ywdqnd3gv4qdqqcqzpgxqyz5vq3q8zqqqp0gsp56tvyu6lx9z5x3e6l52q83qa0u6ermf96hrwpgqtz3y9tufvj6y9q9qyyssq8vqk0pj3ygdc4lx8h53j7xjscys0werym9jjzgwsk4mhuvkgamk42atmphxxs30ek5kxllu33cqyhyes74gc3vm5uhv2czkv74xdjdgp4yar6v";
    String invoice64 = "lightning:obto2147485160:100m1pjr3uqppp5pwkmym4ymgtlf0ty8ycjhpuqgndvg8taff67hjvezx4wk4pk09qqdqqcqzpgxqyz5vq3q8zqqqp0gsp5eqttp6geknwkh72nkuwmmyk4r7cy2tal95nckx6350j2fw0k5vmq9qyyssqq79wf5khcgyd0vss9fvwe7mg4ddmez49zeq73t3y99ntc64fll9zu0lsfu0yuagya5328xpj2kz6r8rs2dw5uygylfhrrk57kw60vnsq66lhgw";
    String invoice65 = "lightning:obto2147485160:100m1pjr3uqdpp573sczd82a0zyl04xl3cmqzqxhycu2ge3yrvwlmyjv686ya8fjufsdqqcqzpgxqyz5vq3q8zqqqp0gsp5ulp3kcsqattrr5e4dfp8gaa99km32vt4rpfeypmjc4v9493yx9rq9qyyssq8ypqcc0l0vclt7k22lukpfekkkgv53spccfqe6wr3z9lrzwqth0qselh74dt3ynnde8rgshuseqad0w6mltwfnx0qtsu5emwzy8lp4sp32fyr7";
    String invoice66 = "lightning:obto2147485160:100m1pjr3uqcpp5u2zagq4neqr0kemu0e230cy6s5re9dekdfsm8phss3zdwhqtujjqdqqcqzpgxqyz5vq3q8zqqqp0gsp5sxs8nsf95sdxqsfcm4fj502wn9u2vlaa6qcsvvyk0026uqpewe5s9qyyssqxh4gk3l8u8hn2y6zntj2qv3h3455h7pumtwk8nudakefgsl49pv96w39rxdjjpt65h4zs52kgq0en6smqz6zstp6nzcllrf8ph8jj4spu3z32v";
    String invoice67 = "lightning:obto2147485160:100m1pjr3up9pp5plr06dl4667c4xsdxe88t4f2avss839zpw37s2wl327fz8s8hduqdqqcqzpgxqyz5vq3q8zqqqp0gsp5r2ppt0xkwuczf40wpmzr96u80mzuhad5zdvx6sptat5lljdl2e0s9qyyssq3v89mak74rjwfqd0ujp66jmzh7px9jghgde7ex7qydnptakltytrqxypv7jwzz9lfej8rx99xptk2j6s0zhlk47fkyztq4us767hcpqqzwwwwg";
    String invoice68 = "lightning:obto2147485160:100m1pjr3up0pp5mhrfw62kdf44cnzxhewrcjnhncrdkyj2fxmjtwgfpttj5lm34nkqdqqcqzpgxqyz5vq3q8zqqqp0gsp5xzs2gu5su0f4pq5e5zwsue69ucc9cw202f963dfa5jqvpth0mrgq9qyyssq2mz90j5fkgmwg3jn8hrwx2wzchu63hngqsgyvd68fdus6exsp5zkwtrju7mf6td0uym63jacfcydk4vwpj9l623ugs9rds65pnw2s9qqd2d5j0";
    String invoice69 = "lightning:obto2147485160:100m1pjr3upcpp5lhpt3fxnuy4cua3kmh88256sazqtgaue3kewke3qfzc03v3h4cqqdqqcqzpgxqyz5vq3q8zqqqp0gsp5f2zzs9frr362p2phmq89xrd047ltmfazhhtak0r0jghh2mk0jv5s9qyyssq7q3g88h6xne34aap2uzu0c5lwsu2hajeptylvh2cnaxhs5yv3sdq56dy0j58ldqa7mukjja8t4eqkdakj50w77zfn7v08zezq9x5zmcpq9wuat";
    String invoice70 = "lightning:obto2147485160:100m1pjr3uzzpp55dxax8zqw4lgyvgat9hez5jrn90qdc8r8xmx8tsswgzwe8r8fqfqdqqcqzpgxqyz5vq3q8zqqqp0gsp59d7lhuujwh0fqxlsgnjgygw3w3d3j47tswtujhkfentl73deeleq9qyyssqssyftvuqc636egsjv0j7k555r7r678zu8e5p86w2gq5cnz2urh44dvqzenyljkdqc93w7q9akxymkc09q6f2y66hh2cj77cqytn3x6sphwqgqg";
    String invoice71 = "lightning:obto2147485160:100m1pjr3ug4pp5sdmejndkhz3j9nu0uf0rwc4ev82fmyk796scmnmrp037n0tdhy7qdqqcqzpgxqyz5vq3q8zqqqp0gsp5nv53as7f367tefqlvj2t3tj77xakq8pweg04sd90h0kcejfw3lcs9qyyssqt9sulwthf5fumgd3k2hlqump97sxgw7eu2xs0lekaq48w5nzx5skrhgr244she3gr0qel8xzns4khpa782sz08428980j7frcjs202sqvle6v4";
    String invoice72 = "lightning:obto2147485160:100m1pjr3ufqpp5a0t9ara8jxzqlqa4km9nanfawt7xjw7chga9qtzv4t4t5l0gdagsdqqcqzpgxqyz5vq3q8zqqqp0gsp50dztp6q7thx2t20xjf96pw4sxt7zfwzysf67l4s8lmsg8yz7kh5q9qyyssqttmfftg90kl707g394vd6murxctqch7lezxupmyedka32qqlp5yp8d7d6wd6hjd5k8fjj9epvg8v9ek63ydh3396tkpl2nfjfm37cdcp92kh2g";
    String invoice73 = "lightning:obto2147485160:100m1pjr3uffpp55xjffh6x0xx7p0uvcjs3783hpjquyc82zxrjjm73fl7zaghkfupsdqqcqzpgxqyz5vq3q8zqqqp0gsp54gwlm68r2gqm4n9vahesy39mr3944f6e436g5p6ky59xvapn2g0s9qyyssq87uyt47u3cygtyr43kmrxg2pjxxwtdkjtlxcyxvqg6d3x6rsehnyd2qtnyav3w6mlcymg6m4czszfzkq0krfll6z0umtl77esqvfq8cp9jv7py";
    String invoice74 = "lightning:obto2147485160:100m1pjr3ufnpp5m5cllyuk6t8wdj0540659fgs0wtsfz3nygaq0pg5kvf6s64u6wgqdqqcqzpgxqyz5vq3q8zqqqp0gsp50jq7n9yjlfrp3zluyagvrwe6y5nc25uw9q63rv7ewyhkptw9m9ns9qyyssqdesvkecsgwgztfv0qjtcmz2yj7anehh5gu66sy50djvaz53346553u450l2yzjhpfcqgxxfqvc62lgggn89vrn7qjtxns5vpp49kkcgpregqx2";
    String invoice75 = "lightning:obto2147485160:100m1pjr3uflpp579ppdjrn7at5xvnujvl7ha4rx8yh0j5ujf4k9ncedftzh68jm0ysdqqcqzpgxqyz5vq3q8zqqqp0gsp5vm4spxwr24mh5cj56e6zmez69l77pgrhskgant4agczlk9g0t8as9qyyssqt99txdy60kewkj6g00pd37e76stfgcww5z9s7hedgjes7c4pxm95fvxrc30c43z6u644ye5f9s36n9d8suny29cr9qh60wuecad9tusqrw8gwt";
    String invoice76 = "lightning:obto2147485160:100m1pjr3u2gpp5aaltj2mt0ergswkj6ekj9walefknv2h8m0pmqva0l4e8fu7zruhsdqqcqzpgxqyz5vq3q8zqqqp0gsp5nupyux4ppnjv8dacspczswnjudf3xfckp7xn22xhy8jlrx4w2hps9qyyssq982efzna89kf2agf9f6z65zqmy86fvh62qezvahaengdlpqdardn2y4df0ayjd92u6qcjfgjzrjlzgah7x6zwu3ztq0lhwuqma2avhgq6huve6";
    String invoice77 = "lightning:obto2147485160:100m1pjr3u2jpp5w9hkrnthnxrqwz444vj96caflv5fxqemsr4z0y4fx0xa8n7zge5sdqqcqzpgxqyz5vq3q8zqqqp0gsp5746uycyek3a7hr2ze6vzme6uwtlsramd0fmu6t3cp05hddzcq08q9qyyssqwullza75tukdua6vd58j9tekq4c9338q2ptwlkfw2glqgsqfctl9k4jgap5wmkflzax07lnrl73hpj0te3awqvpwjw2faxaqvtp8ywspjr72jt";
    String invoice78 = "lightning:obto2147485160:100m1pjr3u2upp5cev92lak2dsf9hdj5g3kwxun9gyh5j4q6qdtc6sdh5dwgy80j02sdqqcqzpgxqyz5vq3q8zqqqp0gsp5wzl0hngygefnfkhj6cx6jpunvz4nuhqfdns5ngp00kjjh9df7grs9qyyssqvkyy866sps4pkarpw7nqe40s0t6vw03zfj72sthgq9su9mgzeswr0jhyct7u55vqd6uc4jq27n9t627j34h4sy264kwc56jguqfrsvgp33khp7";
    String invoice79 = "lightning:obto2147485160:100m1pjr3ut9pp5kl0msgc9cqpzw25cj4mmnuksgzrg95lnuk6rev4x83h6jdf3xcnqdqqcqzpgxqyz5vq3q8zqqqp0gsp5zfl0f4aavqse8rlruy9jdlsjedwmq29lszl75kdnce89kegd22yq9qyyssqwpf7567s9ws36eclzx0q2m99kkakcnqxc526fp6crhtdcy096lpnlsvklszye5cp53xkvenpul0jgwjpvkmjzq4ae7j9clgfuhdmyxsqs0y72c";
    String invoice80 = "lightning:obto2147485160:100m1pjr3utspp5q4u04cf8ljwzs6s24v5njpzf4ajpzku2rqdzx5ml2lm08n0g89usdqqcqzpgxqyz5vq3q8zqqqp0gsp59mlu6nym9vac6dpkmrezsl7trkcptyd2w53vju4hj72s8p7zpjrs9qyyssqkn9drknj7r2p5tme4jgm290x5dplffwf34rlx9h7hsc5r7nvgj7nhyxqjqsqgvqyq2vqgs08dqhtrpk3zhf27l8nnxdjjck8h7k7vcspyf8nu6";
    String invoice81 = "lightning:obto2147485160:100m1pjr3utepp57kp4m5c38hakxx0vg2ldcvw2e42mm5hhu6wgjlvk8avayr8h0ytqdqqcqzpgxqyz5vq3q8zqqqp0gsp5lrw94wje5awnc3sp504y7rqunn0n7xfrfg2aay8ryc9lerdpadzs9qyyssq0vjj3u2suppljfw00g6gjllk6ke5hhzmnjhhztts7c2s3svwnxun5all8c8k44424td3a0wy8ualujtqlvqluvredac892zukelc90sqcahsa8";
    String invoice82 = "lightning:obto2147485160:100m1pjr3uvypp5zc88pkgx3gn803ma0vasyvrz3g5ywu6wnqwxhxn3u7vmt7hvxkcsdqqcqzpgxqyz5vq3q8zqqqp0gsp5fgzwyp6w0g4d9d3fj6kltv29d6u2p8g3wceuad4vvk64h054r8rq9qyyssqs2vwymwvy5z9gtx9sm5d4jnm85mz4c5fa7sa9a3df08vkqy98fekacqn894drl92udtuk4waj9z05xx5f2pfqeqf8p7kevgjyryp3ugpglrrrg";
    String invoice83 = "lightning:obto2147485160:100m1pjr3uvdpp5vyp72t8gw2sd3urgu5qaetyj3202cte3c3meyywh3an5mjulfp5qdqqcqzpgxqyz5vq3q8zqqqp0gsp5h8p245nu22en3mpkq4wkj9g8vn8wtwlqds2n5quml98pvpp5yfcs9qyyssq67eadruwxu3lgeqvfj9jzx0h5aqxxt4xtu3sfc3pfexppqhjwf252kffknhu6wvx69wh66xw5pp9fntj7x2svx233t7gnmqw4td0z6cpcfzpg6";
    String invoice84 = "lightning:obto2147485160:100m1pjr3uv4pp5xee60j5grdv90cxqsn6k73h6xga362q2mdwtlqreu79dxdwu67pqdqqcqzpgxqyz5vq3q8zqqqp0gsp5aexp50gahvqz0yvg3jj5guu3vf4c7yavzf9pf5z4t98483v20hns9qyyssqt9enxdcgje7gzc66ckhwplvzy7zd0uzq8acmaf4dcek9gv7aa6dyym8nc6na9m6aqly7achv0hg6ehay0spqhkjt8mrv2af8595my0gqymk57s";
    String invoice85 = "lightning:obto2147485160:100m1pjr3uvlpp5kka84sq9s99rh9j73ql6u6fdped95dk95k05cp7ftxvszx4un9fsdqqcqzpgxqyz5vq3q8zqqqp0gsp5ujsd5m2tvp02nnae097san59lx6qax8uuw64nefllcs035y67p0q9qyyssqtj93kjxmyw4qlz95pzzy53ezegu33u4z0j2xuv902cv2anl09xf4ygr85gmzcxe0azy5zehfe4rl38fx7qjaedg9yxy462mkl7sxmscp4sqrqh";
    String invoice86 = "lightning:obto2147485160:100m1pjr3udfpp5yplfdzrgmfycw89m6apkmg6707feve2saczzzkhgsgl0qgq2dvtqdqqcqzpgxqyz5vq3q8zqqqp0gsp52x88hckfk0snzxj2uvgmx9pdhg5fuqsgst377p9wx79nux5nfp6s9qyyssq7katwn44e7vxq4djl5ctgkg5p9ptcf7wzsu7jxh0ccpu05ss4wfhquvltwjsqrtfr67qh305qju6k55um84dklm0rwcjrl0atpljqxgp247gkw";
    String invoice87 = "lightning:obto2147485160:100m1pjr3udnpp5ct5w9dtxprm8qmghlkkssq4yqj8r57s8mc96rx3gt0k3mrca7reqdqqcqzpgxqyz5vq3q8zqqqp0gsp52vfklz0aqy559mumpn2p4s9qafy8wn5lh6cx9l3ue4l7sv07597q9qyyssqyfhnqsmhxw36x7l3wyes9mt47me6evwcesp39s6dp6q7hvxxsdtqlsldnjys2ashdcx9tqd97cag66u0rt6aplyqx3ucpfv9mdfy29gpzqp7z0";
    String invoice88 = "lightning:obto2147485160:100m1pjr3udapp553yn84trrmpcmd30spkajrl6rlagusr072uexxuahyjzkfyyhlcqdqqcqzpgxqyz5vq3q8zqqqp0gsp54q4mludmr56gcjnj5gp57x79cm732ugkvlmqmkp3nw54pt97y45s9qyyssqhwzxwhkatjey5e3plsp3j5xjrg0yc84wlq79urjszmyfhfqd8nq5gmhd2egrweqm8gkmmmysdmmza8l3rr7r8cgwhalzl5ygn9zqngqp2zr7gz";
    String invoice89 = "lightning:obto2147485160:100m1pjr3uw8pp5rnuylzjftanhj6cq77kgcqer3uyjfrzw6da23cr7lpl3jv6l8daqdqqcqzpgxqyz5vq3q8zqqqp0gsp5j8eej3xhmwxft53thhzw9rm53vf50jww5muu0qe7f58f38a2tfps9qyyssqucccxd936w3kra5hfy87p4cs55nhdjh9wdv4huxpxurelrvyh5khj3y4srw8e0k8er688qg83gtw3xyhm5qktj4wd7agr6kjftv7k2sp3m5lw9";
    String invoice90 = "lightning:obto2147485160:100m1pjr3uwspp5l59zypqxtwjny8q4nzgkfa3tsyu4jvkts0y2qsau5ltdtggd7v6sdqqcqzpgxqyz5vq3q8zqqqp0gsp5sgcr7dydh05um4kq5yrlxayfleuzu9ssy922hltuqdgs2k68um4q9qyyssqpl2sk29cz69x2tc3edvfk3e9pulm004632t2r38gl2enahczzryjxyqgvn9u2fpsdcxaxtccfsljqj30nnppm7ww2kwmauhksevh05gpgcuadt";
    String invoice91 = "lightning:obto2147485160:100m1pjr3uw6pp5853r8r4azpcfrc8xvsq43vlkkj8sjuzr7tn922leqj5kg2u5t29sdqqcqzpgxqyz5vq3q8zqqqp0gsp5e47gqljzn8rzv02jv9trg99je9vgmf9dhv5uq5nz7p0twr9nhswq9qyyssqnd380v8ukp79eksaupeuq7e6hvp7z2g3thvr5dl5vlsg5ysfyz0z3pyhphf3tahv78fmu2atl9pq3k3925nkk45z38cat2rlp4666ssp7j76l2";
    String invoice92 = "lightning:obto2147485160:100m1pjr3u0ypp59ejrvxx3qslnhrkhrc3phdcvqpzxp6pry0s8mr4cvaq4te4wxlfqdqqcqzpgxqyz5vq3q8zqqqp0gsp5955tgagm4d58c8w89qej6zrypckxfezt3vt5en3qjmzsx7h7gypq9qyyssqjfmgvza8wa3qtthsawg2yrrcweqrp7rz0lg489wd934f7szgtngxw9he73x3z9wh05kcs9peugeeytmwe4uhs8k0v9zk97rplsay9gcpc9vp8z";
    String invoice93 = "lightning:obto2147485160:100m1pjr3u0dpp5fpe8clgszpuv7j478m2wtxpu758e5gvwvrcf5mqs97j858d5npuqdqqcqzpgxqyz5vq3q8zqqqp0gsp567jzd82y26jpw48kzpnmsmxmucvvl66zl50d32w8qca8zj9xglds9qyyssqqtvvcqudl87chjdxsyua3m49a6zhcy0y3mhvmr8efckf786h2ug49mpknrdcd3wpt3gcjpjghfef4yyhd9r32cyv4c5j4hnjvxjjwesq2383lz";
    String invoice94 = "lightning:obto2147485160:100m1pjr3u0cpp5206sdr4mnnc32yddc7gq59pam8sy3elrwuqq0lwjcyrxujth324sdqqcqzpgxqyz5vq3q8zqqqp0gsp5pmz8jpmxfwkl0uh34yetexncyh0zzgt6ttxtj7lacs8ne3xhfx3s9qyyssqdtm64j0eh525l0mzrwgq3646tczew7s5t2ge73tzwhtywxm5jqhsn4rs09f6mvasc5cvm2a046clv5ndwjm7nyxl7fys4p0ysgulrcgpg9yp6p";
    String invoice95 = "lightning:obto2147485160:100m1pjr3usppp597welqq4uv9ux02pa3y7fsfm6xvxfdqa8art7zyv9sstuae70z2sdqqcqzpgxqyz5vq3q8zqqqp0gsp5ytypgwrnkz6vx5tayaxxrurwxsllwfhu9a6tjgnzgk5p858fa0jq9qyyssqxkp4px86fdze7w8rgueam7m7w5ykx5eceqfnklxceqthjerzzjtrcpza79gs9gzsa74xathvqza3zm3qpwegmc3rvcde774hye0g2jgpahw5ku";
    String invoice96 = "lightning:obto2147485160:100m1pjr3us2pp5z8mvmrljep4j044h437z04gk6w35cuysskgj7s2mk9p8w75yt9ksdqqcqzpgxqyz5vq3q8zqqqp0gsp5wv82dcupzvxnwdwvr75kwskqwltrjry7fs4pszu6jntec33265sq9qyyssqmmq0s8wac85nvh7vf9z2cmt36gwzdtyxzvf8jgrydhx2mrcn5zrrcr0f5p5xf0dy3u3mujhmfkecqt69rwwu0rndppy4dwjz7ufjr4qq3yxjy9";
    String invoice97 = "lightning:obto2147485160:100m1pjr3usjpp5r5u0zaqgez8q9n2wc9tnuj6k2vl0nwmkxesep24vxewz4jpc5fwqdqqcqzpgxqyz5vq3q8zqqqp0gsp56xcr5u48ykrg5ufffmakudsl6pd40xshcckhfxrywcxpcy95ymhq9qyyssqvq9ldj4ru6a8qzj00js4sphj7uvratj5vc78ulykxfs8uw7gtqwnx3f4vtq2er0skkafn0cmj7msxesl838h9ph24jyerewhzk0lwdcpjyx6eq";
    String invoice98 = "lightning:obto2147485160:100m1pjr3usapp5fg8tlvpdslz45zf7d3lsx49p6l3fx0lanjjvxssx8zzqq7m9z4csdqqcqzpgxqyz5vq3q8zqqqp0gsp53h3afqq3p2c9mt5cnzkm4jsgq44fx5u7rkn96593ffm2qs2swrhs9qyyssq6k5f6xu6v8ljnc47zhww7nhv6zahsgawj5jzk4rg7uhyh0qmzevptu8xergak0u7jethp83mmr7qpfpqlx8gd3lrhpyyrrmdqhdx2cspmwqqwe";
    String invoice99 = "lightning:obto2147485160:100m1pjr3u3xpp59sndj0fklxe2enzca89a8f2sn8my0quxxgdjv66z9v96r5x56x9qdqqcqzpgxqyz5vq3q8zqqqp0gsp5c8smd7yclw9h0lwcqn0hsrxmwghy27ncsrw68xflhptswx6vxkkq9qyyssqt6skj4gk9h6y7h2fs5avnm5dgxn637da3lt4hxltmepamspx6dvkz4wlpn8kqxu79j2sjj4t4rjhcv4are9em6ajryx77mahg47awaqqpzqq3n";
    String invoice100 = "lightning:obto2147485160:100m1pjr3u3spp5x7yfmkvfr9fg6yeulvxszup6tuahyxzlnk77ndaw03w7g9erkzrqdqqcqzpgxqyz5vq3q8zqqqp0gsp5te27275yaegyuhag2zug0mw28hzyvqp826awyylgtl6vn9rnmzfq9qyyssqpn34azcukwgqazty7w5j6nde7fmrynkahr5s5m47zxhvhjr2dcupyen6h3szak6e30m5fu2hhqxn7ymcdqln7h8dupxxtyd9qs5kwxcpy0zy2x";

    @Override
    protected Drawable getWindowBackground() {
        return ContextCompat.getDrawable(mContext, R.color.color_f9f9f9);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_unlock;
    }

    @Override
    protected void initView() {
        mAssetId = Long.parseLong("2147485160");
    }

    @Override
    protected void initData() {

    }

    @OnClick(R.id.btn_unlock)
    public void clickBtn() {
        LogUtils.e(TAG, "------------------clickBtn------------------");
//        exam1();
        exam2();
    }

    private void exam2() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtils.e("=================","1");
                payInvoice(invoice1);
                payInvoice(invoice2);
                payInvoice(invoice3);
                payInvoice(invoice4);
                payInvoice(invoice5);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e("=================","2");
                        payInvoice(invoice6);
                        payInvoice(invoice7);
                        payInvoice(invoice8);
                        payInvoice(invoice9);
                        payInvoice(invoice10);
                    }
                }).start();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e("=================","3");
                        payInvoice(invoice11);
                        payInvoice(invoice12);
                        payInvoice(invoice13);
                        payInvoice(invoice14);
                        payInvoice(invoice15);
                    }
                }).start();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e("=================","4");
                        payInvoice(invoice16);
                        payInvoice(invoice17);
                        payInvoice(invoice18);
                        payInvoice(invoice19);
                        payInvoice(invoice20);
                    }
                }).start();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e("=================","5");
                        payInvoice(invoice21);
                        payInvoice(invoice22);
                        payInvoice(invoice23);
                        payInvoice(invoice24);
                        payInvoice(invoice25);
                    }
                }).start();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e("=================","6");
                        payInvoice(invoice26);
                        payInvoice(invoice27);
                        payInvoice(invoice28);
                        payInvoice(invoice29);
                        payInvoice(invoice30);
                    }
                }).start();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e("=================","7");
                        payInvoice(invoice31);
                        payInvoice(invoice32);
                        payInvoice(invoice33);
                        payInvoice(invoice34);
                        payInvoice(invoice35);
                    }
                }).start();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e("=================","8");
                        payInvoice(invoice36);
                        payInvoice(invoice37);
                        payInvoice(invoice38);
                        payInvoice(invoice39);
                        payInvoice(invoice40);
                    }
                }).start();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e("=================","9");
                        payInvoice(invoice41);
                        payInvoice(invoice42);
                        payInvoice(invoice43);
                        payInvoice(invoice44);
                        payInvoice(invoice45);
                    }
                }).start();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e("=================","10");
                        payInvoice(invoice46);
                        payInvoice(invoice47);
                        payInvoice(invoice48);
                        payInvoice(invoice49);
                        payInvoice(invoice50);
                    }
                }).start();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e("=================","11");
                        payInvoice(invoice51);
                        payInvoice(invoice52);
                        payInvoice(invoice53);
                        payInvoice(invoice54);
                        payInvoice(invoice55);
                    }
                }).start();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e("=================","12");
                        payInvoice(invoice56);
                        payInvoice(invoice57);
                        payInvoice(invoice58);
                        payInvoice(invoice59);
                        payInvoice(invoice60);
                    }
                }).start();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e("=================","13");
                        payInvoice(invoice61);
                        payInvoice(invoice62);
                        payInvoice(invoice63);
                        payInvoice(invoice64);
                        payInvoice(invoice65);
                    }
                }).start();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e("=================","14");
                        payInvoice(invoice66);
                        payInvoice(invoice67);
                        payInvoice(invoice68);
                        payInvoice(invoice69);
                        payInvoice(invoice70);
                    }
                }).start();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e("=================","15");
                        payInvoice(invoice71);
                        payInvoice(invoice72);
                        payInvoice(invoice73);
                        payInvoice(invoice74);
                        payInvoice(invoice75);
                    }
                }).start();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e("=================","16");
                        payInvoice(invoice76);
                        payInvoice(invoice77);
                        payInvoice(invoice78);
                        payInvoice(invoice79);
                        payInvoice(invoice80);
                    }
                }).start();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e("=================","17");
                        payInvoice(invoice81);
                        payInvoice(invoice82);
                        payInvoice(invoice83);
                        payInvoice(invoice84);
                        payInvoice(invoice85);
                    }
                }).start();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e("=================","18");
                        payInvoice(invoice86);
                        payInvoice(invoice87);
                        payInvoice(invoice88);
                        payInvoice(invoice89);
                        payInvoice(invoice90);
                    }
                }).start();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e("=================","19");
                        payInvoice(invoice91);
                        payInvoice(invoice92);
                        payInvoice(invoice93);
                        payInvoice(invoice94);
                        payInvoice(invoice95);
                    }
                }).start();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e("=================","20");
                        payInvoice(invoice96);
                        payInvoice(invoice97);
                        payInvoice(invoice98);
                        payInvoice(invoice99);
                        payInvoice(invoice100);
                    }
                }).start();
            }
        }).start();
    }

    private void exam1() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LogUtils.e(TAG, "------------------1------------------");
                            Thread.sleep(1000);
                            payInvoice(invoice1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LogUtils.e(TAG, "------------------2------------------");
                            Thread.sleep(1000);
                            payInvoice(invoice2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LogUtils.e(TAG, "------------------3------------------");
                            Thread.sleep(1000);
                            payInvoice(invoice3);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LogUtils.e(TAG, "------------------4------------------");
                            Thread.sleep(1000);
                            payInvoice(invoice4);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LogUtils.e(TAG, "------------------5------------------");
                            Thread.sleep(1000);
                            payInvoice(invoice5);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LogUtils.e(TAG, "------------------6------------------");
                            payInvoice(invoice6);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LogUtils.e(TAG, "------------------7------------------");
                            payInvoice(invoice7);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LogUtils.e(TAG, "------------------8------------------");
                            payInvoice(invoice8);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LogUtils.e(TAG, "------------------9------------------");
                            payInvoice(invoice9);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LogUtils.e(TAG, "------------------10------------------");
                            Thread.sleep(1000);
                            payInvoice(invoice10);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LogUtils.e(TAG, "------------------11------------------");
                            Thread.sleep(1000);
                            payInvoice(invoice11);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LogUtils.e(TAG, "------------------12------------------");
                            Thread.sleep(1000);
                            payInvoice(invoice12);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LogUtils.e(TAG, "------------------13------------------");
                            Thread.sleep(1000);
                            payInvoice(invoice13);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LogUtils.e(TAG, "------------------14------------------");
                            Thread.sleep(1000);
                            payInvoice(invoice14);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LogUtils.e(TAG, "------------------15------------------");
                            Thread.sleep(1000);
                            payInvoice(invoice15);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }
        });
    }

    private void payInvoice(String invoice) {
        invoice = invoice.toLowerCase();
        // Remove the "lightning:" uri scheme if it is present, LND needs it without uri scheme
        final String lnInvoice = UriUtil.removeURI(invoice);
        LightningOuterClass.PayReqString decodePaymentRequest = LightningOuterClass.PayReqString.newBuilder()
                .setPayReq(lnInvoice)
                .build();
        Obdmobile.decodePayReq(decodePaymentRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast(mContext, e.getMessage());
                    }
                });
                LogUtils.e(TAG, "------------------decodePaymentOnError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
                try {
                    LightningOuterClass.PayReq resp = LightningOuterClass.PayReq.parseFrom(bytes);
//                    LogUtils.e(TAG, "------------------decodePaymentOnResponse-----------------" + resp);
                    if (resp == null) {
                        ToastUtils.showToast(mContext, "Probe send request was null");
                        return;
                    }
                    RouterOuterClass.SendPaymentRequest probeRequest;
                    if (mAssetId == 0) {
                        probeRequest = prepareBtcPaymentProbe(resp);
                    } else {
                        probeRequest = preparePaymentProbe(resp);
                    }
                    Obdmobile.routerOB_SendPaymentV2(probeRequest.toByteArray(), new RecvStream() {
                        @Override
                        public void onError(Exception e) {
                            if (e.getMessage().equals("EOF")) {
                                return;
                            }
                            LogUtils.e(TAG, "-------------routerSendPaymentV2OnError-----------" + e.getMessage());
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.showToast(mContext, e.getMessage());
                                }
                            });
                        }

                        @Override
                        public void onResponse(byte[] bytes) {
                            try {
                                LightningOuterClass.Payment payment = LightningOuterClass.Payment.parseFrom(bytes);
//                                LogUtils.e(TAG, "-------------routerSendPaymentV2OnResponse-----------" + payment.toString());
                                switch (payment.getFailureReason()) {
                                    case FAILURE_REASON_INCORRECT_PAYMENT_DETAILS:
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                LightningOuterClass.Route route = payment.getHtlcs(0).getRoute();
                                                String paymentHash = payment.getPaymentHash();
                                                long payAmount;
                                                if (mAssetId == 0) {
                                                    payAmount = resp.getAmtMsat();
                                                } else {
                                                    payAmount = resp.getAmount();
                                                }
                                                showStepPay(lnInvoice, route, paymentHash, payAmount);
                                                deletePaymentProbe(payment.getPaymentHash());
                                            }
                                        });
                                        break;
                                    case FAILURE_REASON_NO_ROUTE:
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                String paymentHash = payment.getPaymentHash();
                                                long payAmount;
                                                if (mAssetId == 0) {
                                                    payAmount = resp.getAmtMsat();
                                                } else {
                                                    payAmount = resp.getAmount();
                                                }
                                                showStepPay(lnInvoice, null, paymentHash, payAmount);
                                                deletePaymentProbe(payment.getPaymentHash());
                                            }
                                        });
                                        break;
                                    default:
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                deletePaymentProbe(payment.getPaymentHash());
                                                LogUtils.e("-----------------error---------------------", lnInvoice);
                                            }
                                        });
                                }
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showStepPay(String lnInvoice, LightningOuterClass.Route route, String paymentHash, long payAmount) {
        if (route != null) {
            RouterOuterClass.SendToRouteRequest sendToRouteRequest = RouterOuterClass.SendToRouteRequest.newBuilder()
                    .setPaymentHash(byteStringFromHex(paymentHash))
                    .setRoute(route)
                    .build();
            Obdmobile.routerSendToRouteV2(sendToRouteRequest.toByteArray(), new Callback() {
                @Override
                public void onError(Exception e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast(mContext, e.getMessage());
                        }
                    });
                    LogUtils.e(TAG, "Exception while executing SendToRoute.");
                    LogUtils.e(TAG, e.getMessage());
                }

                @Override
                public void onResponse(byte[] bytes) {
                    try {
                        LightningOuterClass.HTLCAttempt htlcAttempt = LightningOuterClass.HTLCAttempt.parseFrom(bytes);
                        switch (htlcAttempt.getStatus()) {
                            case SUCCEEDED:
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        LogUtils.e("------------------1--------------------", lnInvoice);
                                    }
                                });
                                break;
                            case FAILED:
                                switch (htlcAttempt.getFailure().getCode()) {
                                    case INCORRECT_OR_UNKNOWN_PAYMENT_DETAILS:
                                        RouterOuterClass.SendPaymentRequest sendPaymentRequest = RouterOuterClass.SendPaymentRequest.newBuilder()
                                                .setAssetId((int) mAssetId)
                                                .setPaymentRequest(lnInvoice)
                                                .setFeeLimitMsat(calculateAbsoluteFeeLimit(payAmount))
                                                .setTimeoutSeconds(RefConstants.TIMEOUT_MEDIUM * RefConstants.TOR_TIMEOUT_MULTIPLIER)
                                                .setMaxParts(1)
                                                .build();
                                        Obdmobile.routerOB_SendPaymentV2(sendPaymentRequest.toByteArray(), new RecvStream() {
                                            @Override
                                            public void onError(Exception e) {
                                                if (e.getMessage().equals("EOF")) {
                                                    return;
                                                }
                                                LogUtils.e(TAG, "------------------routerOB_SendPaymentV2OnError------------------" + e.getMessage());
                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ToastUtils.showToast(mContext, e.getMessage());
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onResponse(byte[] bytes) {
                                                if (bytes == null) {
                                                    return;
                                                }
                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            LightningOuterClass.Payment resp = LightningOuterClass.Payment.parseFrom(bytes);
//                                                            LogUtils.e(TAG, "------------------routerOB_SendPaymentV2OnResponse-----------------" + resp);
                                                            if (resp.getStatus() == LightningOuterClass.Payment.PaymentStatus.SUCCEEDED) {
                                                                LogUtils.e("------------------2--------------------", lnInvoice);
                                                            } else if (resp.getStatus() == LightningOuterClass.Payment.PaymentStatus.FAILED) {
                                                                String errorMessage;
                                                                switch (resp.getFailureReason()) {
                                                                    case FAILURE_REASON_TIMEOUT:
                                                                        errorMessage = mContext.getResources().getString(R.string.error_payment_timeout);
                                                                        ToastUtils.showToast(mContext, errorMessage);
                                                                        break;
                                                                    case FAILURE_REASON_NO_ROUTE:
                                                                        errorMessage = mContext.getResources().getString(R.string.error_payment_no_route);
                                                                        ToastUtils.showToast(mContext, errorMessage);
                                                                        break;
                                                                    case FAILURE_REASON_INSUFFICIENT_BALANCE:
                                                                        errorMessage = mContext.getResources().getString(R.string.error_payment_insufficient_balance);
                                                                        ToastUtils.showToast(mContext, errorMessage);
                                                                        break;
                                                                    case FAILURE_REASON_INCORRECT_PAYMENT_DETAILS:
                                                                        errorMessage = mContext.getResources().getString(R.string.error_payment_invalid_details);
                                                                        ToastUtils.showToast(mContext, errorMessage);
                                                                        break;
                                                                }
                                                            }
                                                        } catch (InvalidProtocolBufferException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                        break;
                                }
                                break;
                        }
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            RouterOuterClass.SendPaymentRequest sendPaymentRequest = RouterOuterClass.SendPaymentRequest.newBuilder()
                    .setAssetId((int) mAssetId)
                    .setPaymentRequest(lnInvoice)
                    .setFeeLimitMsat(calculateAbsoluteFeeLimit(payAmount))
                    .setTimeoutSeconds(RefConstants.TIMEOUT_MEDIUM * RefConstants.TOR_TIMEOUT_MULTIPLIER)
                    .setMaxParts(RefConstants.LN_MAX_PARTS)
                    .build();
            Obdmobile.routerOB_SendPaymentV2(sendPaymentRequest.toByteArray(), new RecvStream() {
                @Override
                public void onError(Exception e) {
                    if (e.getMessage().equals("EOF")) {
                        return;
                    }
                    LogUtils.e(TAG, "------------------noRouterOB_SendPaymentV2OnError------------------" + e.getMessage());
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast(mContext, e.getMessage());
                        }
                    });
                }

                @Override
                public void onResponse(byte[] bytes) {
                    if (bytes == null) {
                        return;
                    }
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                LightningOuterClass.Payment resp = LightningOuterClass.Payment.parseFrom(bytes);
//                                LogUtils.e(TAG, "------------------noRouterOB_SendPaymentV2OnResponse-----------------" + resp);
                                if (resp.getStatus() == LightningOuterClass.Payment.PaymentStatus.SUCCEEDED) {
                                    LogUtils.e("------------------3--------------------", lnInvoice);
                                } else if (resp.getStatus() == LightningOuterClass.Payment.PaymentStatus.FAILED) {
                                    String errorMessage;
                                    switch (resp.getFailureReason()) {
                                        case FAILURE_REASON_TIMEOUT:
                                            errorMessage = mContext.getResources().getString(R.string.error_payment_timeout);
                                            ToastUtils.showToast(mContext, errorMessage);
                                            break;
                                        case FAILURE_REASON_NO_ROUTE:
                                            errorMessage = mContext.getResources().getString(R.string.error_payment_no_route);
                                            ToastUtils.showToast(mContext, errorMessage);
                                            break;
                                        case FAILURE_REASON_INSUFFICIENT_BALANCE:
                                            errorMessage = mContext.getResources().getString(R.string.error_payment_insufficient_balance);
                                            ToastUtils.showToast(mContext, errorMessage);
                                            break;
                                        case FAILURE_REASON_INCORRECT_PAYMENT_DETAILS:
                                            errorMessage = mContext.getResources().getString(R.string.error_payment_invalid_details);
                                            ToastUtils.showToast(mContext, errorMessage);
                                            break;
                                    }
                                }
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        }
    }

    public RouterOuterClass.SendPaymentRequest preparePaymentProbe(LightningOuterClass.PayReq paymentRequest) {
        return preparePaymentProbe(paymentRequest.getDestination(), paymentRequest.getAmount(), paymentRequest.getPaymentAddr(), paymentRequest.getRouteHintsList(), paymentRequest.getFeaturesMap());
    }

    public RouterOuterClass.SendPaymentRequest preparePaymentProbe(String destination, long amountSat, @Nullable ByteString paymentAddress, @Nullable List<LightningOuterClass.RouteHint> routeHints, @Nullable Map<Integer, LightningOuterClass.Feature> destFeatures) {
        // The paymentHash will be replaced with a random hash. This way we can create a fake payment.
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[PAYMENT_HASH_BYTE_LENGTH];
        random.nextBytes(bytes);
        long feeLimit = calculateAbsoluteFeeLimit(amountSat);
        RouterOuterClass.SendPaymentRequest.Builder sprb = RouterOuterClass.SendPaymentRequest.newBuilder()
                .setAssetId((int) mAssetId)
                .setDest(byteStringFromHex(destination))
                .setAssetAmt(amountSat)
                .setFeeLimitMsat(feeLimit)
                .setPaymentHash(ByteString.copyFrom(bytes))
                .setNoInflightUpdates(true)
                .setTimeoutSeconds(RefConstants.TIMEOUT_MEDIUM * RefConstants.TOR_TIMEOUT_MULTIPLIER)
                .setMaxParts(1); // We are looking for a direct path. Probing using MPP isn’t really possible at the moment.
        if (paymentAddress != null) {
            sprb.setPaymentAddr(paymentAddress);
        }
        if (destFeatures != null && !destFeatures.isEmpty()) {
            for (Map.Entry<Integer, LightningOuterClass.Feature> entry : destFeatures.entrySet()) {
                sprb.addDestFeaturesValue(entry.getKey());
            }
        }
        if (routeHints != null && !routeHints.isEmpty()) {
            sprb.addAllRouteHints(routeHints);
        }

        return sprb.build();
    }

    public RouterOuterClass.SendPaymentRequest prepareBtcPaymentProbe(LightningOuterClass.PayReq paymentRequest) {
        return prepareBtcPaymentProbe(paymentRequest.getDestination(), paymentRequest.getAmtMsat(), paymentRequest.getPaymentAddr(), paymentRequest.getRouteHintsList(), paymentRequest.getFeaturesMap());
    }

    public RouterOuterClass.SendPaymentRequest prepareBtcPaymentProbe(String destination, long amountSat, @Nullable ByteString paymentAddress, @Nullable List<LightningOuterClass.RouteHint> routeHints, @Nullable Map<Integer, LightningOuterClass.Feature> destFeatures) {
        // The paymentHash will be replaced with a random hash. This way we can create a fake payment.
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[PAYMENT_HASH_BYTE_LENGTH];
        random.nextBytes(bytes);
        long feeLimit = calculateAbsoluteFeeLimit(amountSat);
        RouterOuterClass.SendPaymentRequest.Builder sprb = RouterOuterClass.SendPaymentRequest.newBuilder()
                .setAssetId((int) mAssetId)
                .setDest(byteStringFromHex(destination))
                .setAmtMsat(amountSat)
                .setFeeLimitMsat(feeLimit)
                .setPaymentHash(ByteString.copyFrom(bytes))
                .setNoInflightUpdates(true)
                .setTimeoutSeconds(RefConstants.TIMEOUT_MEDIUM * RefConstants.TOR_TIMEOUT_MULTIPLIER)
                .setMaxParts(1); // We are looking for a direct path. Probing using MPP isn’t really possible at the moment.
        if (paymentAddress != null) {
            sprb.setPaymentAddr(paymentAddress);
        }
        if (destFeatures != null && !destFeatures.isEmpty()) {
            for (Map.Entry<Integer, LightningOuterClass.Feature> entry : destFeatures.entrySet()) {
                sprb.addDestFeaturesValue(entry.getKey());
            }
        }
        if (routeHints != null && !routeHints.isEmpty()) {
            sprb.addAllRouteHints(routeHints);
        }

        return sprb.build();
    }

    /**
     * Used to delete a payment probe. We don't need these stored in the database. They just bloat it.
     */
    public static void deletePaymentProbe(String paymentHash) {
        LightningOuterClass.DeletePaymentRequest deletePaymentRequest = LightningOuterClass.DeletePaymentRequest.newBuilder()
                .setPaymentHash(byteStringFromHex(paymentHash))
                .setFailedHtlcsOnly(false)
                .build();
        Obdmobile.deletePayment(deletePaymentRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
//                LogUtils.e(TAG, "Exception while deleting payment probe.");
//                LogUtils.e(TAG, e.getMessage());

            }

            @Override
            public void onResponse(byte[] bytes) {
//                LogUtils.e(TAG, "Payment probe deleted.");
            }
        });
    }

    // ByteString values when using for example "paymentRequest.getDescriptionBytes()" can for some reason not directly be used as they are double in length
    private static ByteString byteStringFromHex(String hexString) {
        byte[] hexBytes = BaseEncoding.base16().decode(hexString.toUpperCase());
        return ByteString.copyFrom(hexBytes);
    }

    public static long calculateAbsoluteFeeLimit(long amountSatToSend) {
        long absFee;
        if (amountSatToSend <= RefConstants.LN_PAYMENT_FEE_THRESHOLD) {
            absFee = (long) (Math.sqrt(amountSatToSend));
        } else {
            absFee = (long) (getRelativeSettingsFeeLimit() * amountSatToSend);
        }
        return Math.max(absFee, 3L);
    }

    public static float getRelativeSettingsFeeLimit() {
        String lightning_feeLimit = "3%";
        String feePercent = lightning_feeLimit.replace("%", "");
        float feeMultiplier = 1f;
        if (!feePercent.equals("None")) {
            feeMultiplier = Integer.parseInt(feePercent) / 100f;
        }
        return feeMultiplier;
    }
}