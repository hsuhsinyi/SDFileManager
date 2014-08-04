package com.example.sdfilemanager;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;

/**
 * @author hhy
 * ����Ŀ¼��������ͼƬ������ͼ
 */
public class ShowAllImage extends Activity {
	private GridView mGridView;
	private List<String> list;
	private ShowAllImageAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showallimage);
		mGridView = (GridView) findViewById(R.id.child_grid);
		list = getIntent().getStringArrayListExtra("data");
		adapter = new ShowAllImageAdapter(this, list, mGridView);
		mGridView.setAdapter(adapter);
		
	}

	@Override
	public void onBackPressed() {
		//Toast.makeText(this, "ѡ�� " + adapter.getSelectItems().size() + " item", Toast.LENGTH_LONG).show();
		super.onBackPressed();
	}
	
}
