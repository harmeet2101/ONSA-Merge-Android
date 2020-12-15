package co.uk.depotnet.onsa.fragments.briefings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

import co.uk.depotnet.onsa.R;

public class PdfReaderFragment extends Fragment {
    private static final String ARG_Briefings = "Docs";
    private String bagDocument;
    private TextView pdf_title;
    private PDFView pdfView;
    private String mFilePath;
    public static PdfReaderFragment newInstance(String fileurl) {
        PdfReaderFragment readerFragment=new PdfReaderFragment();
        Bundle bundle=new Bundle();
        bundle.putString(ARG_Briefings, fileurl);
        readerFragment.setArguments(bundle);
        return readerFragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            bagDocument = args.getString(ARG_Briefings);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pdf_reader, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View reader, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(reader, savedInstanceState);
        pdf_title=reader.findViewById(R.id.pdf_title);
        pdfView=reader.findViewById(R.id.pdfView);
        pdfView.fromFile(new File(bagDocument));
    }
}