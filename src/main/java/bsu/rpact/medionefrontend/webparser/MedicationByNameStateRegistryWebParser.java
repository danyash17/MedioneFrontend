package bsu.rpact.medionefrontend.webparser;

import bsu.rpact.medionefrontend.entity.medical.RegistryMedication;
import bsu.rpact.medionefrontend.utils.mapper.RegistryMedicationMapper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

public class MedicationByNameStateRegistryWebParser implements WebParser<List<RegistryMedication>> {

    public final static String SEARCH_URL = "https://www.rceth.by/Refbank/reestr_lekarstvennih_sredstv/results";
    private final static String QUERY_STRING_BEFORE_VALUE = "FOpt.VAn[=]False[;]FOpt.VUnTerm[=]False[;]FOpt.VPause[=]False[;]FOpt.VFiles[=]True[;]FOpt.VEField1[=]False[;]FOpt.OrderBy[=]N_LP[;]FOpt.DirOrder[=]asc[;]FOpt.VT[=]t[;]FOpt.PageC[=]100[;]FOpt.PageN[=]1[;]FOpt.CRec[=]147[;]FOpt.CPage[=]2[;]FProps[0].Name[=]N_LP[;]FProps[0].IsText[=]True[;]FProps[0].CritElems[0].Val[=]";
    private final static String QUERY_STRING_AFTER_VALUE = "[;]FProps[0].CritElems[0].Excl[=]False[;]FProps[0].CritElems[0].Crit[=]Like[;]FProps[0].CritElems[0].Num[=]1[;]FProps[1].Name[=]N_MP[;]FProps[1].IsText[=]True[;]FProps[1].CritElems[0].Val[=]_[;]FProps[1].CritElems[0].Excl[=]False[;]FProps[1].CritElems[0].Crit[=]Like[;]FProps[1].CritElems[0].Num[=]1[;]FProps[2].Name[=]Type[;]FProps[2].IsDrop[=]True[;]FProps[2].CritElems[0].Val[=]_[;]FProps[2].CritElems[0].Excl[=]False[;]FProps[2].CritElems[0].Crit[=]Like[;]FProps[2].CritElems[0].Num[=]1[;]FProps[3].Name[=]ATC[;]FProps[3].IsText[=]True[;]FProps[3].CritElems[0].Val[=]_[;]FProps[3].CritElems[0].Excl[=]False[;]FProps[3].CritElems[0].Crit[=]Like[;]FProps[3].CritElems[0].Num[=]1[;]FProps[4].Name[=]N_FR[;]FProps[4].IsText[=]True[;]FProps[4].CritElems[0].Val[=]_[;]FProps[4].CritElems[0].Excl[=]False[;]FProps[4].CritElems[0].Crit[=]Like[;]FProps[4].CritElems[0].Num[=]1[;]FProps[5].Name[=]N_FV[;]FProps[5].IsText[=]True[;]FProps[5].CritElems[0].Val[=]_[;]FProps[5].CritElems[0].Excl[=]False[;]FProps[5].CritElems[0].Crit[=]Like[;]FProps[5].CritElems[0].Num[=]1[;]FProps[6].Name[=]Company_Declarant[;]FProps[6].IsText[=]True[;]FProps[6].CritElems[0].Val[=]_[;]FProps[6].CritElems[0].Excl[=]False[;]FProps[6].CritElems[0].Crit[=]Like[;]FProps[6].CritElems[0].Num[=]1[;]FProps[7].Name[=]NREG[;]FProps[7].IsText[=]True[;]FProps[7].CritElems[0].Val[=]_[;]FProps[7].CritElems[0].Excl[=]False[;]FProps[7].CritElems[0].Crit[=]Like[;]FProps[7].CritElems[0].Num[=]1[;]FProps[8].Name[=]Data[;]FProps[8].IsDate[=]True[;]FProps[8].CritElemsD.Val1[=]null[;]FProps[8].CritElemsD.Val2[=]null[;]FProps[8].CritElemsD.Crit[=]Equal[;]FProps[9].Name[=]TERM[;]FProps[9].IsDate[=]True[;]FProps[9].CritElemsD.Val1[=]null[;]FProps[9].CritElemsD.Val2[=]null[;]FProps[9].CritElemsD.Crit[=]Equal[;]7";
    private final String cookies;

    public MedicationByNameStateRegistryWebParser() {
        Connection.Response searchPageResponse = null;
        try {
            searchPageResponse = Jsoup.connect(SEARCH_URL).method(Connection.Method.GET).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.cookies = searchPageResponse.cookies().toString();
    }

    @Override
    public List<RegistryMedication> parse(String nameSearchQuery) {
        try {
            String[] arr = cookies.substring(1, cookies.length() - 1).split("=");
            String sessionId = arr[1];
            String queryString = Base64.getEncoder().encodeToString((QUERY_STRING_BEFORE_VALUE + nameSearchQuery + QUERY_STRING_AFTER_VALUE).getBytes());
            Connection.Response response = Jsoup.connect(SEARCH_URL)
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                    .header("Accept-Language", "ru,ru-RU;q=0.9,en-US;q=0.8,en;q=0.7")
                    .header("Cache-Control", "max-age=0")
                    .header("Connection", "keep-alive")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Cookie", "ASP.NET_SessionId=" + sessionId + "; _ga=GA1.2.651377047.1680534456; _gid=GA1.2.400230576.1680876280; _gat=1")
                    .header("Origin", "https://www.rceth.by")
                    .header("Referer", "https://www.rceth.by/Refbank/reestr_lekarstvennih_sredstv/results")
                    .header("Sec-Fetch-Dest", "document")
                    .header("Sec-Fetch-Mode", "navigate")
                    .header("Sec-Fetch-Site", "same-origin")
                    .header("Sec-Fetch-User", "?1")
                    .header("Upgrade-Insecure-Requests", "1")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36 OPR/96.0.0.0")
                    .header("sec-ch-ua", "\"Not=A?Brand\";v=\"8\", \"Chromium\";v=\"110\", \"Opera GX\";v=\"96\"")
                    .header("sec-ch-ua-mobile", "?0")
                    .header("sec-ch-ua-platform", "\"Windows\"")
                    .data("QueryStringFind", queryString)
                    .data("IsPostBack", "False")
                    .data("PropSubmit", "")
                    .data("ValueSubmit", "")
                    .data("VFiles", "True")
                    .data("FProps[0].IsText", "True")
                    .data("FProps[0].Name", "N_LP")
                    .data("FProps[0].CritElems[0].Num", "1")
                    .data("FProps[0].CritElems[0].Val", nameSearchQuery)
                    .data("FProps[0].CritElems[0].Crit", "Like")
                    .data("FProps[0].CritElems[0].Excl", "false")
                    .data("FProps[1].IsText", "True")
                    .data("FProps[1].Name", "N_MP")
                    .data("FProps[1].CritElems[0].Num", "1")
                    .data("FProps[1].CritElems[0].Val", "")
                    .data("FProps[1].CritElems[0].Crit", "Like")
                    .data("FProps[1].CritElems[0].Excl", "false")
                    .data("FProps[2].IsDrop", "True")
                    .data("FProps[2].Name", "Type")
                    .data("FProps[2].CritElems[0].Num", "1")
                    .data("FProps[2].CritElems[0].Val", "")
                    .data("FProps[2].CritElems[0].Excl", "false")
                    .data("FProps[3].IsText", "True")
                    .data("FProps[3].Name", "N_FR")
                    .data("FProps[3].CritElems[0].Num", "1")
                    .data("FProps[3].CritElems[0].Val", "")
                    .data("FProps[3].CritElems[0].Crit", "Like")
                    .data("FProps[3].CritElems[0].Excl", "false")
                    .data("FProps[4].IsText", "True")
                    .data("FProps[4].Name", "N_FV")
                    .data("FProps[4].CritElems[0].Num", "1")
                    .data("FProps[4].CritElems[0].Val", "")
                    .data("FProps[4].CritElems[0].Crit", "Like")
                    .data("FProps[4].CritElems[0].Excl", "false")
                    .data("FProps[5].IsText", "True")
                    .data("FProps[5].Name", "Company_Declarant")
                    .data("FProps[5].CritElems[0].Num", "1")
                    .data("FProps[5].CritElems[0].Val", "")
                    .data("FProps[5].CritElems[0].Crit", "Like")
                    .data("FProps[5].CritElems[0].Excl", "false")
                    .data("FProps[6].IsText", "True")
                    .data("FProps[6].Name", "NREG")
                    .data("FProps[6].CritElems[0].Num", "1")
                    .data("FProps[6].CritElems[0].Val", "")
                    .data("FProps[6].CritElems[0].Crit", "Like")
                    .data("FProps[6].CritElems[0].Excl", "false")
                    .data("FProps[7].IsDate", "True")
                    .data("FProps[7].Name", "Data")
                    .data("FProps[7].CritElemsD.Val1", "")
                    .data("FProps[7].CritElemsD.Crit", "Equal")
                    .data("FProps[7].CritElemsD.Val2", "")
                    .data("FProps[8].IsDate", "True")
                    .data("FProps[8].Name", "TERM")
                    .data("FProps[8].CritElemsD.Val1", "")
                    .data("FProps[8].CritElemsD.Crit", "Equal")
                    .data("FProps[8].CritElemsD.Val2", "")
                    .data("FOpt.PageC", "100")
                    .data("FOpt.OrderBy", "N_LP")
                    .data("FOpt.DirOrder", "asc")
                    .data("FOpt.VFiles", "true")
                    .data("FOpt.VFiles", "false")
                    .data("FOpt.VEField1", "false")
                    .method(Connection.Method.POST)
                    .execute();
            Document searchResultsPage = response.parse();
            Element table = searchResultsPage.select("div.results div.table-view table").first();
            Elements rows = table.select("tbody tr");
            return new RegistryMedicationMapper().map(rows);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
