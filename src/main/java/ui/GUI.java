/**
 * MIT License
 *
 * Copyright (c) 2019 c0ny1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package ui;

import burp.BurpExtender;
import com.sun.codemodel.internal.JOp;
import entity.CaptchaEntity;
import entity.HttpService;
import entity.MatchResult;
import entity.Rule;
import sun.jvm.hotspot.code.UncommonTrapBlob;
import utils.HttpClient;
import utils.RuleMannager;
import utils.Util;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GUI {
    private JPanel MainPanel;
    public JSplitPane spImg;
    private JSplitPane spInterface;
    private JSplitPane spOption;
    private JSplitPane spAll;

    //获取验证码面板
    private JLabel lbURL;
    private JTextField tfURL;
    private JButton btnGetCaptcha;
    private JTextArea taRequest;
    private JLabel lbCaptcha;
    private JLabel lbImage;
    private JToggleButton tlbLock;
    private JTextArea taResponse;

    //接口配置编码
    private JPanel plInterfaceReq;
    private JPopupMenu pmInterfaceMenu;
    private JTabbedPane tpInterfaceReq;
    private JTextArea taInterfaceTmplReq;
    private JTextArea taInterfaceRawReq;
    private JLabel lbInterfaceURL;
    private JTextField tfInterfaceURL;
    private JButton btnIdentify;
    private JMenu menuTmplManager = new JMenu("模版");
    private JMenuItem miGeneralTmpl = new JMenuItem("通用模版");
    private JMenuItem miTesseract = new JMenuItem("tesseract-ocr-web");
    private JMenuItem miBaiduOCR = new JMenuItem("百度OCR");
    private JMenuItem miCNNCaptcha = new JMenuItem("cnn_captcha");
    private JMenuItem miImageRaw = new JMenuItem("验证码图片二进制内容标签");
    private JMenuItem miBase64Encode = new JMenuItem("Base64编码标签");
    private JMenuItem miURLEncode = new JMenuItem("URL编码标签");
    private JTabbedPane tpInterfaceRsq;
    private JPanel plInterfaceRsq;
    //private JTextArea taInterfaceRsq;
    private JTextPane InterfaceRsq;
    private JLabel lbRuleType = new JLabel("规则类型:");
    private JComboBox cbmRuleType;
    private JLabel lbRegular = new JLabel("匹配规则:");
    private JTextField tfRegular;
    private JButton btnSaveTmpl;

    //识别结果面板
    private JPanel plResult;
    private JTable table;
    private JScrollPane spTable;
    private TableModel model;

    //一些公共变量
    private byte[] byteImg;
    public static final List<CaptchaEntity> captcha = new ArrayList<CaptchaEntity>();


    public JTextField getTfURL(){
        return tfURL;
    }

    public JTextArea getTaRequest(){
        return taRequest;
    }

    public JTextArea getTaResponse(){
        return taResponse;
    }

    public JButton getBtnGetCaptcha(){
        return this.btnGetCaptcha;
    }

    public JTable getTable(){
        return table;
    }

    public TableModel getModel(){
        return this.model;
    }

    public String getCaptchaURL(){
        return this.tfURL.getText();
    }

    public String getCaptchaReqRaw(){
        return this.taRequest.getText();
    }

    public JTextField getInterfaceURL(){
        return this.tfInterfaceURL;
    }

    public JTextArea getTaInterfaceTmplReq(){
        return this.taInterfaceTmplReq;
    }

    public JTextArea getInterfaceReqRaw(){
        return this.taInterfaceRawReq;
    }

    public JTextField getRegular(){
        return this.tfRegular;
    }

    public JComboBox getCbmRuleType(){
        return this.cbmRuleType;
    }

    public GUI(){
        MainPanel = new JPanel();
        MainPanel.setBorder(new EmptyBorder(2, 2, 2, 2));
        MainPanel.setLayout(new BorderLayout(0, 0));

        //图片获取面板
        lbURL = new JLabel("验证码URL:");
        tfURL = new JTextField(30);
        btnGetCaptcha = new JButton("获取");
        btnGetCaptcha.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(tfURL.getText().equals(null) || tfURL.getText().trim().equals("")){
                    JOptionPane.showMessageDialog(null,"请设置验证码URL","captcha-killer提示",JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if(taRequest.getText().equals(null) || taRequest.getText().trim().equals("")){
                    JOptionPane.showMessageDialog(null,"请设置获取验证码的请求数据包","captcha-killer提示",JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if(!Util.isURL(tfURL.getText())){
                    JOptionPane.showMessageDialog(null,"验证码URL不合法！","captcha-killer提示",JOptionPane.WARNING_MESSAGE);
                    return;
                }

                GetCaptchaThread thread = new GetCaptchaThread(tfURL.getText(),taRequest.getText());
                thread.start();
            }
        });
        taRequest = new JTextArea();
        taRequest.setLineWrap(true);
        taRequest.setWrapStyleWord(true);//断行不断字
        JScrollPane spRequest = new JScrollPane(taRequest);

        JPanel imgLeftPanel = new JPanel();
        imgLeftPanel.setLayout(new GridBagLayout());
        GBC gbc_lburl = new GBC(0,0,1,1).setFill(GBC.HORIZONTAL).setInsets(3,3,0,0);
        GBC gbc_tfurl = new GBC(1,0,1,1).setFill(GBC.HORIZONTAL).setWeight(100,1).setInsets(3,3,0,0);
        GBC gbc_btngetcaptcha = new GBC(2,0,11,1).setInsets(3,3,0,3);
        GBC gbc_tarequst = new GBC(0,1,100,100).setFill(GBC.BOTH).setWeight(100,100).setInsets(3,3,3,3);
        imgLeftPanel.add(lbURL,gbc_lburl);
        imgLeftPanel.add(tfURL,gbc_tfurl);
        imgLeftPanel.add(btnGetCaptcha,gbc_btngetcaptcha);
        imgLeftPanel.add(spRequest,gbc_tarequst);

        JPanel imgRigthPanel = new JPanel();
        imgRigthPanel.setLayout(new GridBagLayout());
        lbImage = new JLabel("");
        lbCaptcha = new JLabel("验证码:");
        tlbLock = new JToggleButton("锁定");
        tlbLock.setToolTipText("当配置好所有选项后，请锁定防止配置被改动！");
        tlbLock.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                boolean isSelected = tlbLock.isSelected();
                if(isSelected){
                    tlbLock.setText("解锁");
                    tfURL.setEnabled(false);
                    taRequest.setEnabled(false);
                    btnGetCaptcha.setEnabled(false);
                    taResponse.setEnabled(false);
                    tfInterfaceURL.setEnabled(false);
                    btnIdentify.setEnabled(false);
                    taInterfaceTmplReq.setEnabled(false);
                    cbmRuleType.setEnabled(false);
                    taInterfaceRawReq.setEnabled(false);
                    tfRegular.setEnabled(false);
                    btnSaveTmpl.setEnabled(false);
                    InterfaceRsq.setEnabled(false);
                }else{
                    tlbLock.setText("锁定");
                    tfURL.setEnabled(true);
                    taRequest.setEnabled(true);
                    btnGetCaptcha.setEnabled(true);
                    taResponse.setEnabled(true);
                    tfInterfaceURL.setEnabled(true);
                    btnIdentify.setEnabled(true);
                    taInterfaceTmplReq.setEnabled(true);
                    cbmRuleType.setEnabled(true);
                    taInterfaceRawReq.setEnabled(true);
                    tfRegular.setEnabled(true);
                    btnSaveTmpl.setEnabled(true);
                    InterfaceRsq.setEnabled(true);
                }
                tlbLock.setSelected(isSelected);
            }
        });
        taResponse = new JTextArea();
        taResponse.setLineWrap(true);
        taResponse.setWrapStyleWord(true);//断行不断字
        taResponse.setEditable(false);
        JScrollPane spResponse = new JScrollPane(taResponse);

        GBC gbc_lbcaptcha = new GBC(0,0,1,1).setFill(GBC.BOTH).setInsets(3,3,0,0);
        GBC gbc_lbimage = new GBC(1,0,1,100).setFill(GBC.BOTH).setWeight(100,1).setInsets(3,3,0,0);
        GBC gbc_tlblock = new GBC(2,0,1,1).setFill(GBC.BOTH).setInsets(3,3,0,3);
        GBC gbc_taresponse = new GBC(0,100,100,100).setFill(GBC.BOTH).setWeight(100,100).setInsets(3,3,3,3);

        imgRigthPanel.add(lbCaptcha,gbc_lbcaptcha);
        imgRigthPanel.add(lbImage,gbc_lbimage);
        imgRigthPanel.add(tlbLock,gbc_tlblock);
        imgRigthPanel.add(spResponse,gbc_taresponse);
        spImg = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        spImg.setResizeWeight(0.5);
        spImg.setLeftComponent(imgLeftPanel);
        spImg.setRightComponent(imgRigthPanel);

        // 识别接口配置面板
        spInterface = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        spInterface.setResizeWeight(0.5);
        plInterfaceReq = new JPanel();
        plInterfaceReq.setLayout(new GridBagLayout());

        lbInterfaceURL = new JLabel("接口URL:");
        tfInterfaceURL = new JTextField(30);
        btnIdentify = new JButton("识别");
        btnIdentify.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(byteImg == null){
                    JOptionPane.showMessageDialog(null,"请先获取要识别的图片","captcha-killer提示",JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if(!Util.isImage(byteImg)){
                    JOptionPane.showMessageDialog(null,"要识别的不是图片，请重新获取！","captcha-killer提示",JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if(tfInterfaceURL.getText().trim() == null || tfInterfaceURL.getText().trim().equals("")){
                    JOptionPane.showMessageDialog(null,"请设置好接口URL","captcha-killer提示",JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if(taInterfaceTmplReq.getText().trim() == null|| taInterfaceTmplReq.getText().trim().equals("")){
                    JOptionPane.showMessageDialog(null,"请设置调用接请求数据包","captcha-killer提示",JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if(tfRegular.getText().trim() == null|| tfRegular.getText().trim().equals("")){
                    JOptionPane.showMessageDialog(null,"请设置好匹配结果的正则","captcha-killer提示",JOptionPane.WARNING_MESSAGE);
                    return;
                }

                IdentifyCaptchaThread thread = new IdentifyCaptchaThread(tfInterfaceURL.getText(),taInterfaceTmplReq.getText(),byteImg);
                thread.start();
            }
        });
        tpInterfaceReq = new JTabbedPane();
        taInterfaceTmplReq = new JTextArea();
        taInterfaceTmplReq.setLineWrap(true);
        taInterfaceTmplReq.setWrapStyleWord(true);
        JScrollPane spInterfaceReq = new JScrollPane(taInterfaceTmplReq);
        taInterfaceRawReq = new JTextArea();
        taInterfaceRawReq.setLineWrap(true);
        taInterfaceRawReq.setWrapStyleWord(true);
        taInterfaceRawReq.setEditable(false);
        JScrollPane spInterfaceRawReq = new JScrollPane(taInterfaceRawReq);
        tpInterfaceReq.addTab("Requst template",spInterfaceReq);
        tpInterfaceReq.addTab("Requst raw",spInterfaceRawReq);
        pmInterfaceMenu = new JPopupMenu();
        menuTmplManager.add(miGeneralTmpl);
        menuTmplManager.add(miTesseract);
        menuTmplManager.add(miBaiduOCR);
        menuTmplManager.add(miCNNCaptcha);
        pmInterfaceMenu.add(menuTmplManager);
        pmInterfaceMenu.addSeparator();
        pmInterfaceMenu.add(miImageRaw);
        pmInterfaceMenu.add(miBase64Encode);
        pmInterfaceMenu.add(miURLEncode);
        miGeneralTmpl.addActionListener(new MenuActionManger());
        miTesseract.addActionListener(new MenuActionManger());
        miBaiduOCR.addActionListener(new MenuActionManger());
        miCNNCaptcha.addActionListener(new MenuActionManger());
        menuTmplManager.addActionListener(new MenuActionManger());
        miImageRaw.addActionListener(new MenuActionManger());
        miBase64Encode.addActionListener(new MenuActionManger());
        miURLEncode.addActionListener(new MenuActionManger());

        taInterfaceTmplReq.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                    pmInterfaceMenu.show(taInterfaceTmplReq, e.getX(), e.getY());
                }
            }
        });


        GBC gbc_lbinterfaceurl = new GBC(1,0,1,1).setFill(GBC.HORIZONTAL).setInsets(3,3,0,0);
        GBC gbc_tfinterfaceurl = new GBC(2,0,1,1).setFill(GBC.HORIZONTAL).setInsets(3,3,0,0).setWeight(100,1);
        GBC gbc_btnidentify = new GBC(3,0,1,1).setFill(GBC.HORIZONTAL).setInsets(3,3,0,3);
        GBC gbc_tpinterfacereq = new GBC(0,1,100,100).setFill(GBC.BOTH).setWeight(100,100).setInsets(3,3,3,3);
        plInterfaceReq.add(lbInterfaceURL,gbc_lbinterfaceurl);
        plInterfaceReq.add(tfInterfaceURL,gbc_tfinterfaceurl);
        plInterfaceReq.add(btnIdentify,gbc_btnidentify);
        plInterfaceReq.add(tpInterfaceReq,gbc_tpinterfacereq);

        plInterfaceRsq = new JPanel();
        plInterfaceRsq.setLayout(new GridBagLayout());
        String[] str = new String[]{"Response data","Regular expression","Define the start and end positions","Defines the start and end strings"};
        cbmRuleType = new JComboBox(str);
        cbmRuleType.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                switch (e.getStateChange()){
                    case ItemEvent.SELECTED:
                        if(e.getItem().equals("Response data")){
                            tfRegular.setText("response_data");
                            tfRegular.setEnabled(false);
                        }else{
                            tfRegular.setEnabled(true);
                        }
                        break;
                }
            }
        });
        tfRegular = new JTextField(30);
        btnSaveTmpl = new JButton("匹配");
        btnSaveTmpl.setToolTipText("用于测试编写的规则是否正确");
        btnSaveTmpl.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Style keywordStyle = ((StyledDocument) InterfaceRsq.getDocument()).addStyle("Keyword_Style", null);
                StyleConstants.setBackground(keywordStyle, Color.YELLOW);
                Style normalStyle = ((StyledDocument) InterfaceRsq.getDocument()).addStyle("Keyword_Style", null);
                StyleConstants.setForeground(normalStyle, Color.BLACK);
                ((StyledDocument) InterfaceRsq.getDocument()).setCharacterAttributes(0,InterfaceRsq.getText().length(),normalStyle,true);

                int type = cbmRuleType.getSelectedIndex();
                String rule = tfRegular.getText();
                Rule ruleEntity = new Rule(type,rule);
                MatchResult result = RuleMannager.match(InterfaceRsq.getText(),ruleEntity);



                //JOptionPane.showMessageDialog(null,Util.getStringCount(InterfaceRsq.getText(),System.lineSeparator())-1,"", JOptionPane.WARNING_MESSAGE);
                int offest = result.getStart();
                int length = result.getEnd() - result.getStart();
                int count = 0;
                if(type == 0){
                    //IndexOf获取到的字符串的位置和JTextPanel面板中的位置不一致，这是由于换行符号造成的。故需要前者减去换行符的个数就等于后者。
                    String rspRaw = InterfaceRsq.getText();
                    rspRaw = rspRaw.substring(0,rspRaw.indexOf(result.getResult()));
                    //为了兼容win和*nix，故要分别统计\r和\n
                    int rCount = Util.getStringCount(rspRaw,"\r");
                    int nCount = Util.getStringCount(rspRaw,"\n");
                    if(rCount > 0){
                        count = rCount;
                    }
                    if(nCount > 0){
                        count = nCount;
                    }
                    offest -= count;
                }

                ((StyledDocument) InterfaceRsq.getDocument()).setCharacterAttributes(offest,length,keywordStyle,true);
            }
        });
        tpInterfaceRsq = new JTabbedPane();

        InterfaceRsq = new JTextPane();
        InterfaceRsq.setEditable(true);
        final JPopupMenu pppInterfaceRsq = new JPopupMenu();
        JMenuItem miMarkIdentifyResult = new JMenuItem("标记为识别结果");
        miMarkIdentifyResult.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int start = InterfaceRsq.getSelectionStart();
                int end = InterfaceRsq.getSelectionEnd();
                String raw = InterfaceRsq.getText();
                switch (cbmRuleType.getSelectedIndex()){
                    case Rule.RULE_TYPE_REGULAR:
                        String regular = RuleMannager.generateRegular(raw,start,end);
                        tfRegular.setText(regular);
                        break;
                    case Rule.RULE_TYPE_POSISTION:
                        String rule1 = RuleMannager.generatePositionRule(start,end);
                        tfRegular.setText(rule1);
                        break;
                    case Rule.RULE_TYPE_START_END_STRING:
                        String rule2 = RuleMannager.generateStartEndRule(raw,start,end);
                        tfRegular.setText(rule2);
                        break;
                    default:
                        break;
                }

            }
        });
        pppInterfaceRsq.add(miMarkIdentifyResult);
        InterfaceRsq.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //当选择匹配模式为Response data时，不显示邮件菜单
                if(cbmRuleType.getSelectedIndex() == 0){
                    return;
                }
                if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                    pppInterfaceRsq.show(InterfaceRsq, e.getX(), e.getY());
                }
            }
        });

        //JScrollPane spInterfaceRsq = new JScrollPane(taInterfaceRsq);
        JScrollPane spInterfaceRsq = new JScrollPane(InterfaceRsq);
        tpInterfaceRsq.addTab("Response raw",spInterfaceRsq);
        GBC gbc_lbruletype = new GBC(0,0,1,1).setFill(GBC.HORIZONTAL).setInsets(3,3,0,0);
        GBC gbc_cbmruletype = new GBC(1,0,1,1).setFill(GBC.HORIZONTAL).setInsets(3,3,0,0);
        GBC gbc_lbregular = new GBC(2,0,1,1).setFill(GBC.HORIZONTAL).setInsets(3,3,0,0);
        GBC gbc_tfregular = new GBC(3,0,1,1).setFill(GBC.HORIZONTAL).setInsets(3,3,0,0).setWeight(100,1);
        GBC gbc_btnsavetmpl = new GBC(4,0,1,1).setFill(GBC.HORIZONTAL).setInsets(3,3,0,3);
        GBC gbc_tpinterfacersq = new GBC(0,1,100,100).setFill(GBC.BOTH).setWeight(100,100).setInsets(3,3,3,3);
        plInterfaceRsq.add(lbRuleType,gbc_lbruletype);
        plInterfaceRsq.add(cbmRuleType,gbc_cbmruletype);
        plInterfaceRsq.add(lbRegular,gbc_lbregular);
        plInterfaceRsq.add(tfRegular,gbc_tfregular);
        plInterfaceRsq.add(btnSaveTmpl,gbc_btnsavetmpl);
        plInterfaceRsq.add(tpInterfaceRsq,gbc_tpinterfacersq);
        spInterface.setLeftComponent(plInterfaceReq);
        spInterface.setRightComponent(plInterfaceRsq);

        //识别结果面板
        plResult = new JPanel();
        final JPopupMenu pppMenu = new JPopupMenu();
        JMenuItem miClear = new JMenuItem("清空");
        miClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (captcha){
                    captcha.clear();
                    model.fireTableDataChanged();
                }
            }
        });
        pppMenu.add(miClear);

        table = new JTable();
        model = new TableModel(table);
        table.setModel(model);
        table.setAutoCreateRowSorter(true);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                    pppMenu.show(table, e.getX(), e.getY());
                }
            }
        });
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int row = table.getSelectedRow();
                if(row != -1) {
                    taInterfaceRawReq.setText(new String(captcha.get(row).getReqRaw()));
                    InterfaceRsq.setText(new String(captcha.get(row).getRsqRaw()));
                }
            }
        });

        spTable = new JScrollPane(table);
        plResult.setLayout(new GridBagLayout());
        GBC gbc_taresult = new GBC(0,0,100,100).setFill(GBC.BOTH).setWeight(100,100).setInsets(3,3,3,3);
        plResult.add(spTable,gbc_taresult);

        //面板合并
        spOption = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        spOption.setResizeWeight(0.5);
        spOption.setTopComponent(spImg);
        spOption.setBottomComponent(spInterface);
        spAll = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        spAll.setResizeWeight(0.85);
        spAll.setLeftComponent(spOption);
        spAll.setRightComponent(plResult);

        MainPanel.add(spAll);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                BurpExtender.callbacks.customizeUiComponent(spAll);
                BurpExtender.callbacks.customizeUiComponent(MainPanel);
            }
        });
    }

    public static byte[] requestImage(String url,String raw){
        if(Util.isURL(url)) {
            HttpClient http = new HttpClient(url, raw, null);
            byte[] rsp = http.doReust();
            BurpExtender.gui.getTaResponse().setText(new String(rsp));
            int BodyOffset = BurpExtender.helpers.analyzeResponse(rsp).getBodyOffset();
            int body_length = rsp.length - BodyOffset;
            byte[] byteImg = Util.subBytes(rsp, BodyOffset, body_length);
            return byteImg;
        }else{
            BurpExtender.stderr.println("[-] captcha URL format invalid");
            return null;
        }
    }

    public class GetCaptchaThread extends Thread {
        private String url;
        private String raw;

        public GetCaptchaThread(String url,String raw) {
            this.url = url;
            this.raw = raw;
        }

        public void run() {
            btnGetCaptcha.setEnabled(false);
            //清洗验证码URL
            HttpService service = new HttpService(url);
            tfURL.setText(service.toString());

            try {
                byte[] byteRes = requestImage(url,raw);
                if(Util.isImage(byteRes)){
                    byteImg =  byteRes;
                }else{
                    lbImage.setText("获取到的不是图片文件！");
                    return;
                }

                ImageIcon icon = Util.byte2img(byteImg);
                lbImage.setIcon(icon);
                lbImage.setText("");
            } catch (Exception e) {
                BurpExtender.stderr.println(e.getMessage());
            }finally {
                BurpExtender.gui.getBtnGetCaptcha().setEnabled(true);
            }
        }
    }

    public static CaptchaEntity identifyCaptcha(String url,String raw,byte[] byteImg,int type,String pattern){
        CaptchaEntity cap = new CaptchaEntity();
        cap.setImage(byteImg);
        HttpClient http = new HttpClient(url, raw, byteImg);
        cap.setReqRaw(http.buildRequstPackget().getBytes());
        byte[] rsp = http.doReust();
        cap.setRsqRaw(rsp);;
        String rspRaw = new String(rsp);

        Rule rule = new Rule(type,pattern);
        MatchResult result = RuleMannager.match(rspRaw, rule);
        cap.setResult(result.getResult());
        //排查请求速度过快可能会导致
        BurpExtender.stdout.println("---------------------------------------------");
        BurpExtender.stdout.println(rspRaw);
        BurpExtender.stdout.println("[+] res = " + result.getResult());
        return cap;
    }

    public class IdentifyCaptchaThread extends Thread{
        private String url;
        private String raw;
        private byte[] img;
        private String pattern;

        public IdentifyCaptchaThread(String url,String raw,byte[] img){
            this.url = url;
            this.raw = raw;
            this.img = img;
            this.pattern = tfRegular.getText();
        }

        @Override
        public void run() {

            int type = cbmRuleType.getSelectedIndex();
            Rule myRule = new Rule(type,tfRegular.getText());

            InterfaceRsq.setText("");
            btnIdentify.setEnabled(false);
            //清洗接口URL
            HttpService service = new HttpService(url);
            tfInterfaceURL.setText(service.toString());

            HttpClient http = new HttpClient(url,raw,byteImg);
            String raw = http.buildRequstPackget();
            taInterfaceRawReq.setText(raw);
            byte[] rsp = http.doReust();
            String rspRaw = new String(rsp);
            InterfaceRsq.setText(rspRaw);
            btnIdentify.setEnabled(true);

            MatchResult result = RuleMannager.match(rspRaw,myRule);
            CaptchaEntity cap = new CaptchaEntity();
            cap.setImage(img);
            cap.setReqRaw(raw.getBytes());
            cap.setRsqRaw(rsp);
            cap.setResult(result.getResult());

            synchronized (captcha){
                int row = captcha.size();
                captcha.add(cap);
                model.fireTableRowsInserted(row,row);
            }
        }
    }

    public class MenuActionManger implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            try {
                if (e.getSource() == miBaiduOCR) {
                    tfInterfaceURL.setText("https://aip.baidubce.com:443");
                    taInterfaceTmplReq.setText("POST /rest/2.0/ocr/v1/accurate?access_token=24.77f0182dc1c96e633010712ab483f123.2592000.1573295509.282335-17479921 HTTP/1.1\n" +
                            "Host: aip.baidubce.com\n" +
                            "Connection: close\n" +
                            "Cache-Control: max-age=0\n" +
                            "Upgrade-Insecure-Requests: 1\n" +
                            "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36\n" +
                            "Sec-Fetch-Mode: navigate\n" +
                            "Sec-Fetch-User: ?1\n" +
                            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3\n" +
                            "Sec-Fetch-Site: none\n" +
                            "Accept-Encoding: gzip, deflate\n" +
                            "Accept-Language: zh-CN,zh;q=0.9\n" +
                            "Content-Type: application/x-www-form-urlencoded\n" +
                            "Content-Length: 55\n" +
                            "\n" +
                            "image=<urlencode><base64>{IMG_RAW}</base64></urlencode>");
                    cbmRuleType.setSelectedIndex(Rule.RULE_TYPE_REGULAR);
                    tfRegular.setText("\"words\"\\: \"(.*?)\"\\}");
                }else if(e.getSource() == miImageRaw){
                    int n = taInterfaceTmplReq.getSelectionStart();
                    taInterfaceTmplReq.insert("{IMG_RAW}",n);
                }else if(e.getSource() == miBase64Encode){
                    int start = taInterfaceTmplReq.getSelectionStart();
                    int end = taInterfaceTmplReq.getSelectionEnd();
                    String newStr = String.format("<base64>%s</base64>",taInterfaceTmplReq.getSelectedText());
                    StringBuffer sbRaw = new StringBuffer(taInterfaceTmplReq.getText());
                    sbRaw.replace(start,end,newStr);
                    taInterfaceTmplReq.setText(sbRaw.toString());
                }else if(e.getSource() == miURLEncode){
                    int start = taInterfaceTmplReq.getSelectionStart();
                    int end = taInterfaceTmplReq.getSelectionEnd();
                    String newStr = String.format("<URLEncode>%s</URLEncode>",taInterfaceTmplReq.getSelectedText());
                    StringBuffer sbRaw = new StringBuffer(taInterfaceTmplReq.getText());
                    sbRaw.replace(start,end,newStr);
                    taInterfaceTmplReq.setText(sbRaw.toString());
                }

            } catch (Exception ex) {
            }
        }
    }
    public Component getComponet(){
        return MainPanel;
    }
}

