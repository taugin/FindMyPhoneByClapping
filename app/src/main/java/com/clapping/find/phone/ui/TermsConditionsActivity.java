package com.clapping.find.phone.ui;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.clapping.find.phone.app.AdHelper;
import com.clapping.find.phone.databinding.ActivityTermsConditionsBinding;
import com.clapping.find.phone.remote.RCManager;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TermsConditionsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdHelper.loadAllInterstitial(this);
        ActivityTermsConditionsBinding binding = ActivityTermsConditionsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        Map<String, String> termsMap = new HashMap<>();
        String termsConditionsEN = "Publishing rules that need to be noted before starting interaction on our website/app:\n" +
                "\n We guarantee that you have the rights and qualifications to post all forms of content you intend to share. Nudity is absolutely not allowed on the app/website.\n" +
                "\n Content that directs traffic to online pornographic services, or that depicts sexual content that depicts extreme sexual acts, or sexually explicit text, images, or sounds is not allowed. Please note that we may make exceptions to our rules based on: Such as artistic, educational, historical, documentary or scientific nature, or other significant benefits to the general public.\n" +
                "\n Encouraging or praising terrorism, organized crime or hate groups is not allowed on our app/site. Also, the sale of sexual services, weapons or harmful drugs is not allowed. We will exclude serious legitimate threats. We We don't like offensive content/speech or intentional targeting of individuals. Attacking or abuse based on skin color, place of origin, gender, gender identity, sexual orientation, religion, disability or disease will not be tolerated.\n" +
                "\n Graphic violence is not allowed. To ensure that our app/site is appropriate for everyone, we may remove videos or images of intense violence.\n" +
                "\n Individuals or accounts found engaging in the restricted activities listed above may be subject to arrest for similar offenses. If viewers observe violations of the above rules, they have the right to report the content to us using the designated reporting feature.\n" +
                "\n" +
                "\n";
        termsMap.put("en", termsConditionsEN);

        String termConditionsJA = "当社の Web サイト/アプリケーションでインタラクションを開始する前に注意する必要がある投稿ルール:\n" +
                "\n あなたが共有しようとしているあらゆる形式のコンテンツを投稿する権利と資格を保持していることを保証します。アプリケーション/ウェブサイトでのヌーディズムは決して許可しません。\n" +
                "\n ポルノ オンライン サービスへのトラフィックを誘導するコンテンツの投稿行為など、極端な性的行為を描写した性的コンテンツ、性的露骨なテキスト、画像、または音声コンテンツの投稿は許可されません。当社は以下に基づいて例外を設ける場合があることに注意してください。 芸術的、教育的、歴史的、ドキュメンタリー的、または科学的な性質、または一般大衆にとってその他の重大な利益がある場合。\n" +
                "私たちのアプリケーション/ウェブサイトでは、テロ、組織された犯罪現場、または憎悪団体に対する奨励や賞賛を許可しません。また、性的サービスや武器、有害な薬物を販売しようとすることも許可されていません。私たちは、深刻な合理的な脅威を排除します。 刺激的なコンテンツ/スピーチ、および私人を意図的に指向することを嫌います。肌の色、出身地、性別、性別、性同一性、性的指向、宗教、障害、病気に基づく攻撃や虐待は許可されません。\n" +
                "\n グラフィック暴力は許可されていません。当社のアプリケーション/ウェブサイトがすべての人にとって適切なものであることを確認するために、激しい暴力のビデオや画像を削除する場合があります。\n" +
                "\n 上記の制限された活動を行っている人物またはアカウントが発見された場合、同様の罪で逮捕され、逮捕される可能性があります。視聴者が上記のルールの違反を発見した場合、指定された報告機能を使用してコンテンツを当社に報告する権利があります。\n" +
                "\n" +
                "\n";
        termsMap.put("ja", termConditionsJA);

        String termConditionsKO = "저희 웹 사이트/애플리케이션을 이용하기 전에 주의해야 할 게시 규칙:\n" +
                "\n 여러분이 공유하려는 모든 유형의 콘텐츠를 게시할 권리와 자격이 있다고 보증합니다. 애플리케이션/웹사이트에서는 누드를 용납하지 않습니다.\n" +
                "\n 포르노 온라인 서비스로의 트래픽 유도나 극단적인 성적인 행위를 묘사한 성적 콘텐츠, 성적으로 노골적인 텍스트, 이미지 또는 음성 콘텐츠를 게시할 수 없습니다. 저희는 아래의 예외를 설정할 수 있음을 유의하십시오. 예술적, 교육적, 역사적, 문서화, 또는 과학적 성향 또는 대중에게 중요한 이유가 있는 경우.\n" +
                "\n 저희 애플리케이션/웹사이트에서는 테러, 조직된 범죄 현장 또는 혐오 단체를 장려하거나 찬양할 수 없습니다. 또한 성적 서비스, 무기, 유해한 약물을 판매하려는 시도 역시 허용되지 않습니다. 저희는 심각한 합리적인 위협을 배제합니다. 자극적인 콘텐츠/언어와 개인을 고의로 대상화하는 것을 용납하지 않습니다. 피부 색상, 출신지, 성별, 성 정체성, 성적 지향, 종교, 장애, 질병을 기반으로 한 공격 또는 학대는 허용되지 않습니다.\n" +
                "\n 그래픽 폭력 역시 허용되지 않습니다. 저희 애플리케이션/웹사이트가 모든 사람에게 적합한지 확인하기 위해 격렬한 폭력의 비디오나 이미지를 삭제할 수 있습니다.\n" +
                "\n 위에서 제한된 활동을 하는 사람 또는 계정이 발견된 경우, 유사한 범죄로 체포될 가능성이 있습니다. 시청자가 위 규칙을 위반한 것을 발견한 경우, 저희에게 내용을 신고할 권리가 있습니다. " +
                "\n" +
                "\n";
        termsMap.put("ko", termConditionsKO);

        String termConditionsZH = "在我们的网站/应用程序上开始互动之前需要注意的发布规则：\n" +
                "\n 我们保证您具有发布您打算分享的所有形式内容的权利和资格。在应用程序/网站上，裸露是绝对不被允许的。\n" +
                "\n 不允许发布涉及引导流量到在线色情服务的内容，或者描述极端性行为的性内容，或性暴露的文本、图像或声音内容。请注意，根据以下情况，我们可能会制定例外规则，如艺术性、教育性、历史性、纪录片性或科学性质，或对一般大众有其他重大利益的情况。\n" +
                "\n 在我们的应用程序/网站上，不允许鼓励或赞扬恐怖主义、有组织犯罪活动或仇恨团体。同时，不允许销售性服务、武器或有害药物。我们将排除严重的合理威胁。我们不喜欢刺激性内容/言论，也不喜欢故意针对个人，不允许基于肤色、出生地、性别、性别认同、性取向、宗教、残疾或疾病进行攻击或虐待。\n" +
                "\n 不允许图形暴力。为确保我们的应用程序/网站对所有人都合适，我们可能会删除激烈暴力的视频或图像。\n" +
                "\n 如果发现从事上述受限制的活动的个人或帐户，可能会因类似罪行被逮捕。如果观众发现违反上述规则，他们有权使用指定的举报功能将内容报告给我们。\n" +
                "\n" +
                "\n";
        termsMap.put("zh", termConditionsZH);

        String language = getLanguage(this);
        String termsConditions = termsMap.get(language);
        if (TextUtils.isEmpty(termsConditions)) {
            termsConditions = termsConditionsEN;
        }
        binding.textView.setText(termsConditions);
    }

    private static String getLanguage(Context context) {
        String language = "en";
        try {
            Locale locale = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                locale = context.getResources().getConfiguration().getLocales().get(0);
            } else {
                locale = context.getResources().getConfiguration().locale;
            }
            language = locale.getLanguage().toLowerCase(Locale.ENGLISH);
        } catch (Exception e) {
        }
        return language;
    }

    @Override
    public void onBackPressed() {
        if (RCManager.isAdUser(this)) {
            AdHelper.showInterstitialCallback(getApplicationContext(), "si_back_terms", new Runnable() {
                @Override
                public void run() {
                    TermsConditionsActivity.super.onBackPressed();
                }
            });
        } else {
            TermsConditionsActivity.super.onBackPressed();
        }
    }
}