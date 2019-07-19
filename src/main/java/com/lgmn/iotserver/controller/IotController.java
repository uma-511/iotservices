package com.lgmn.iotserver.controller;

import com.lgmn.common.result.Result;
import com.lgmn.iotserver.command.ControlCommand;
import com.lgmn.iotserver.command.LoginCommand;
import com.lgmn.iotserver.command.SwitchScreenCommand;
import com.lgmn.iotserver.command.UmaCommand;
import com.lgmn.iotserver.dto.LoginDto;
import com.lgmn.iotserver.model.ControlEntity;
import com.lgmn.iotserver.model.LoginEntity;
import com.lgmn.iotserver.model.PrintPojo;
import com.lgmn.iotserver.server.*;
import com.lgmn.iotserver.services.*;
import com.lgmn.iotserver.utils.CoderUtils;
import com.lgmn.iotserver.utils.ExcelUtils;
import com.lgmn.iotserver.utils.PrintUtil;
import com.lgmn.umaservices.basic.dto.LabelFormatDto;
import com.lgmn.umaservices.basic.dto.ViewLabelRecordDto;
import com.lgmn.umaservices.basic.dto.ViewOrderInfoDto;
import com.lgmn.umaservices.basic.entity.LabelFormatEntity;
import com.lgmn.umaservices.basic.entity.LabelRecordEntity;
import com.lgmn.umaservices.basic.entity.ViewLabelRecordEntity;
import com.lgmn.umaservices.basic.entity.ViewOrderInfoEntity;
import com.lgmn.userservices.basic.entity.LgmnUserEntity;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Component
public class IotController {

    private static final Logger logger = LoggerFactory.getLogger(IotController.class.getName());
    @Autowired
    UmaCommand umaCommand;

    @Autowired
    SwitchScreenCommand switchScreenCommand;

    @Autowired
    LoginCommand loginCommand;

    @Autowired
    ControlCommand controlCommand;

    @Autowired
    ViewLabelRecordApiService viewLabelRecordApiService;

    @Autowired
    ViewOrderInfoApiService viewOrderInfoApiService;

    @Autowired
    UserService userService;

    @Autowired
    LabelRecordService labelRecordService;

    @Autowired
    LabelFormatService labelFormatService;

    public IotController(ViewLabelRecordApiService viewLabelRecordService,ViewOrderInfoApiService viewOrderInfoService, UserService userService,LabelRecordService labelRecordService, LabelFormatService labelFormatService){
        umaCommand = new UmaCommand();
        switchScreenCommand = new SwitchScreenCommand();
        loginCommand = new LoginCommand();
        controlCommand = new ControlCommand();
        viewLabelRecordApiService = viewLabelRecordService;
        viewOrderInfoApiService = viewOrderInfoService;
        this.userService=userService;
        this.labelRecordService=labelRecordService;
        this.labelFormatService=labelFormatService;
    }

    /**
     * 消息处理入口
     * @param ctx
     * @param msg
     */
    public void handler(ChannelHandlerContext ctx, Object msg) {

        String clientIP = SaveChannel(ctx);
        logger.info("server receive message :"+clientIP+":"+ msg);

        String msgStr = msg.toString().replace("[","").replace("]","").toUpperCase();

//        msgStr = msgStr.substring(1,msgStr.length()-2);

        String eventStart="EE B1 11 ";
        String buttonEventEnd=" FF FC FF FF";
        String inputEventEnd=" 00 FF FC FF FF";

        /**
         * 操作界面 单号输入事件
         */
        String orderNoString=eventStart + UmaCommand.SCREEN.CONTROLLER.getCommand() + " " + UmaCommand.CONTROLLER.ORDER_NO.getCommand();

        /**
         * 操作界面 包号输入事件
         */
        String packNum = eventStart + UmaCommand.SCREEN.CONTROLLER.getCommand() + " " +  UmaCommand.CONTROLLER.PACK_NUM.getCommand();

        logger.warn(msgStr);

        String event = EventMap.get(clientIP);

        // 判断事件类型
        if(StringUtils.isEmpty(event) && StringUtils.startsWith(msgStr,orderNoString.toUpperCase()) && StringUtils.endsWith(msgStr,inputEventEnd)){
            msgStr =  msgStr.replaceFirst(eventStart,"");
            msgStr=msgStr.substring(0,msgStr.length()-inputEventEnd.length());
            event_order_no(ctx,msgStr);
        }else if(StringUtils.isEmpty(event) &&StringUtils.startsWith(msgStr,packNum.toUpperCase()) && StringUtils.endsWith(msgStr,inputEventEnd)){
            msgStr =  msgStr.replaceFirst(eventStart,"");
            msgStr=msgStr.substring(0,msgStr.length()-inputEventEnd.length());
            event_pack_num(ctx,msgStr);
        }else if(StringUtils.startsWith(msgStr,eventStart) && StringUtils.endsWith(msgStr,inputEventEnd)){
            //输入框事件
            msgStr = msgStr.replaceFirst(eventStart,"");
            msgStr=msgStr.substring(0,msgStr.length()-inputEventEnd.length());

            inputEvent(ctx,msgStr);
        }else if(StringUtils.startsWith(msgStr,eventStart) && StringUtils.endsWith(msgStr,buttonEventEnd)){
            //按钮事件
            msgStr = msgStr.replaceFirst(eventStart,"");
            msgStr=msgStr.substring(0,msgStr.length()-buttonEventEnd.length());

            buttonEvent(ctx,msgStr);
        }
    }

    private void event_order_no(ChannelHandlerContext ctx, String msgStr) {
        /**
         * todo 单号输入操作
         * 1. 获取订单信息
         * 2. 更新屏幕数据
         */
        logger.info("event_order_no");
        String orderNo=getValue(msgStr);
        String ip = getIPAddress(ctx);
        logger.info(orderNo);

        ViewOrderInfoEntity orderInfoEntity=getOrderInfoByOrderNo(orderNo,ip);

        sendCommand(ctx,controlCommand.clean(UmaCommand.CONTROLLER.TIPMSG));
        List<String> commands= getOrderInfoCommands(orderNo, ip, orderInfoEntity);
//        commands.add(controlCommand.clean(UmaCommand.CONTROLLER.TIPMSG));
        sendCommand(ctx,commands);

    }

    /**
     * 输入订单号调用
     * 根据订单号获取订单信息
     * @param orderNo
     * @return
     */
    private ViewOrderInfoEntity getOrderInfoByOrderNo(String orderNo,String ip) {
        ViewOrderInfoDto viewOrderInfoDto = new ViewOrderInfoDto();
        viewOrderInfoDto.setOrderNo(orderNo);
        ViewOrderInfoEntity viewOrderInfoEntity = viewOrderInfoApiService.findByDto(viewOrderInfoDto);
        return viewOrderInfoEntity;
    }

    private List<String> getOrderInfoCommands(String orderNo, String ip, ViewOrderInfoEntity viewOrderInfoEntity) {
        LoginEntity loginEntity = LoginMap.get(ip);
        ControlEntity controlEntity= ControlMap.get(ip);
        List<String> commands=new ArrayList<>();
        if(viewOrderInfoEntity==null) {
            List<String> ckeanCommands=controlCommand.cleanOrderInfo();
            commands.addAll(ckeanCommands);
            commands.add(controlCommand.setOrderNo(orderNo));
            commands.add(controlCommand.setTipMsg("找不到订单信息"));
            loginEntity.setViewOrderInfoEntity(null);
        }else{
            LoginMap.get(ip).setViewOrderInfoEntity(viewOrderInfoEntity);
            String labelNum=viewOrderInfoEntity.getOrderNo();
            if (StringUtils.isEmpty(labelNum)) {
                labelNum = Long.toString(System.currentTimeMillis());
            }
            commands.add(controlCommand.setOrderNo(labelNum));
            String width = viewOrderInfoEntity.getWidth().toString().replace(".00","");
            commands.add(controlCommand.setWidth(width));
            commands.add(controlCommand.setSpec(viewOrderInfoEntity.getSpecs()));
            commands.add(controlCommand.setModelName(viewOrderInfoEntity.getModelName()));
            commands.add(controlCommand.setQuantity(viewOrderInfoEntity.getPerPackQuantity().toString()));
            commands.add(controlCommand.setFloor(viewOrderInfoEntity.getFloor().toString()));
            commands.add(controlCommand.setColorNum(viewOrderInfoEntity.getColor()));
            commands.add(controlCommand.setPackNum(viewOrderInfoEntity.getPackNum().toString()));
            controlEntity.setOrderNo(viewOrderInfoEntity.getOrderNo());
            controlEntity.setCurrPackNum(viewOrderInfoEntity.getPackNum()-1);
            controlEntity.setNextPackNum(viewOrderInfoEntity.getPackNum());
            BigDecimal metre=viewOrderInfoEntity.getLongs();
            String ma=viewOrderInfoEntity.getYard();
            String m="0";
            if(metre.compareTo(new BigDecimal(m))>0 && StringUtils.isEmpty(ma)){
                m=metre.toString();
            }else if(metre.compareTo(new BigDecimal(m))<=0 && StringUtils.isNotEmpty(ma)){
                m=ma;
            }
//            commands.add(controlCommand.setMetre(m));
            commands.add(controlCommand.setWeightTotal(viewOrderInfoEntity.getCumulativeWeight().toString()));
            commands.add(controlCommand.setQuanTotal(viewOrderInfoEntity.getCumulativeQuantity().toString()));
        }
        return commands;
    }

    private void event_pack_num(ChannelHandlerContext ctx, String msgStr) {
        /**
         * todo 包号输入操作
         * 1. 根据单号+包号获取信息
         * 2, 根据标签状态获取设置包号输入框颜色 （出库）蓝色：00 1F （作废）红色：F8 00 （包号错误）黄色： FF E0 （正常）白色： 9F E6
         */
        logger.info("event_pack_num");
        String packNum=getValue(msgStr);
        logger.info(packNum);
        String ip = getIPAddress(ctx);
        ControlEntity controlEntity = ControlMap.get(ip);
        String orderNo=controlEntity.getOrderNo();
        sendCommand(ctx,controlCommand.clean(UmaCommand.CONTROLLER.TIPMSG));
        List<String> commands = getLabelByOrderNoAndPackNum(packNum, controlEntity, orderNo,ip);
//        commands.add(controlCommand.clean(UmaCommand.CONTROLLER.TIPMSG));
        sendCommand(ctx,commands);
    }

    private List<String> getLabelByOrderNoAndPackNum(String packNum, ControlEntity controlEntity, String orderNo,String ip) {
        int intpackNum=Integer.parseInt(packNum);
        ViewLabelRecordDto dto = new ViewLabelRecordDto();
        dto.setOrderNo(orderNo);
        if(StringUtils.isNotEmpty(packNum)) {
            dto.setPackId(intpackNum);
        }
        List<String> commands=new ArrayList<>();
        LoginEntity loginEntity = LoginMap.get(ip);
        ViewLabelRecordEntity viewLabelRecordEntity = viewLabelRecordApiService.findByDto(dto);
        if(viewLabelRecordEntity==null) {
            commands.add(controlCommand.setOrderNo(orderNo));
            if(intpackNum>controlEntity.getNextPackNum()) {
                commands.add(controlCommand.setPackNumColor("FF E0"));
                commands.add(controlCommand.setTipMsg("当前最大流水号："+controlEntity.getNextPackNum()));
            }else if(intpackNum==controlEntity.getNextPackNum()){
                commands.add(controlCommand.setPackNumColor("9F E6"));
                commands.add(controlCommand.clean(UmaCommand.CONTROLLER.TIPMSG));
            }else if(intpackNum<controlEntity.getNextPackNum()){
                commands.add(controlCommand.setPackNumColor("FF E0"));
                commands.add(controlCommand.setTipMsg("流水号缺失："+packNum));
            }
            controlEntity.setStatus(999);
            loginEntity.setLabelRecordEntity(null);
        }else{
            LoginMap.get(ip).setLabelRecordEntity(viewLabelRecordEntity);
            controlEntity.setStatus(viewLabelRecordEntity.getStatus());

            commands.add(controlCommand.setOrderNo(viewLabelRecordEntity.getOrderNo()));
            commands.add(controlCommand.setModelName(viewLabelRecordEntity.getModelName()));
            commands.add(controlCommand.setWidth(viewLabelRecordEntity.getWidth().toString().replace(".00","")));
            commands.add(controlCommand.setSpec(viewLabelRecordEntity.getSpecs()));
            commands.add(controlCommand.setQuantity(Integer.toString(viewLabelRecordEntity.getQuantity())));
            commands.add(controlCommand.setFloor(viewLabelRecordEntity.getFloor().toString()));
            commands.add(controlCommand.setColorNum(viewLabelRecordEntity.getColor()));
            BigDecimal metre=viewLabelRecordEntity.getLongs();
            String ma=viewLabelRecordEntity.getYard();
            String m="0";
            if(metre.compareTo(new BigDecimal(m))>0 && StringUtils.isEmpty(ma)){
                m=metre.toString();
            }else if(metre.compareTo(new BigDecimal(m))<=0 && StringUtils.isNotEmpty(ma)){
                m=ma;
            }
            int status = viewLabelRecordEntity.getStatus();
            switch (status){
                case 0:
                case 1:
                    commands.add(controlCommand.setTipMsg("标签已打印"));
                    commands.add(controlCommand.setPackNumColor("9F E6"));
                    break;
                case 8:
                    commands.add(controlCommand.setTipMsg("标签已作废"));
                    commands.add(controlCommand.setPackNumColor("F8 00"));
                    break;
                default:
                    commands.add(controlCommand.setTipMsg("标签已出库"));
                    commands.add(controlCommand.setPackNumColor("00 1F"));
                    break;
            }
        }

//        logger.info(viewLabelRecordEntity.getModelName());
        return commands;
    }

    /**
     * 按钮事件
     * @param ctx
     * @param msgStr
     */
    private void buttonEvent(ChannelHandlerContext ctx, String msgStr) {
        String modelName=getScreen(msgStr);
        String event=getControl(modelName,msgStr);

        String ip = getIPAddress(ctx);
        logger.info("|" + event + "|");
        logger.info(msgStr);
        try {
            Method method=this.getClass().getMethod(event,ChannelHandlerContext.class);
            method.invoke(this,ctx);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 输入框事件
     * @param ctx
     * @param msgStr
     */
    private void inputEvent(ChannelHandlerContext ctx, String msgStr) {
        String[] msgs = msgStr.split(" 00 FF FC FF FF EE B1 11 ");

        Object obj = new Object();
        Method method = null;
        String modelName = "";
        String button = "";
        String value = "";
        String ip = "";
        String event = "";
        for (String msg:msgs) {
            modelName = getScreen(msg);
            button = getControl(modelName, msg);

            /**
             * 当控件名称以 "event_" 开头表示这是一个按钮，直接跳过
             */
            if(button == null || StringUtils.startsWith(button,"event_") || button.equals("")){
                continue;
            }
            logger.info(msg);
            value = getValue(msg);
            ip = getIPAddress(ctx);
            event = EventMap.get(ip);

            if (modelName.equals("LoginEntity")) {
                obj = LoginMap.get(ip);
            } else if (modelName.equals("ControlEntity")) {
                obj = ControlMap.get(ip);
            }

            try {
                method = obj.getClass().getMethod("set" + button, String.class);
                method.invoke(obj, value);
                Method[] methods=obj.getClass().getMethods();
                method = obj.getClass().getMethod("isFinish");
                boolean finish = (boolean)method.invoke(obj);
                /**
                 * 获取界面数据时，需要整个界面重新获取并保存到对应的会话界面实体对象
                 * 当获取界面数据完成且由界面按钮触发时执行
                 * 完成状态由获取界面最后一个输入控件的值时更改为true
                 * 如果不是由界面触发，event为空字符串
                  */
                if(finish && StringUtils.isNotEmpty(event)){
                    method=this.getClass().getMethod(event+"_finish",ChannelHandlerContext.class);
                    method.invoke(this,ctx);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * ============================================================================
     * ==== begin ================ 登录界面按钮事件 ===============================
     * ============================================================================
     */
    /**
     * 登录界面确认按钮处理方法 - 开始
     * @param ctx
     */
    public void event_login_confirm(ChannelHandlerContext ctx){
        String ip = getIPAddress(ctx);
        LoginEntity loginEntity = LoginMap.get(ip);

        /**
         * 开始获取数据签，保存会话事件，并把完成状态设为false
         */
        EventMap.add(ip,"event_login_confirm");
        loginEntity.setFinish(false);
        sendCommand(ctx,loginCommand.getUName());
        sendCommand(ctx,loginCommand.getPwd());
        sendCommand(ctx,loginCommand.getBC());
        sendCommand(ctx,loginCommand.getJT());
    }

    /**
     * 登录界面 确认按钮 处理方法 - 完成
     * @param ctx
     */
    public void event_login_confirm_finish(ChannelHandlerContext ctx){
        String ip = getIPAddress(ctx);
        LoginEntity loginEntity = LoginMap.get(ip);
        /**
         * 完成数据获取后，清空会话事件
         */
        EventMap.add(ip,"");

        /**
         * todo 登录操作
         * 1. 登录成功跳转操作界面
         *   1-1 获取操作界面信息并发送
         * 2. 登录失败跳转错误界面
         */

        String userName=loginEntity.getUsername();
        String password=loginEntity.getPassword();
        String banci=loginEntity.getBanci()+"班";
        String jitai=loginEntity.getJitai();
        jitai=jitai.length()==1? "0"+jitai : jitai;
        jitai=jitai+"机台";

        List<String> commands=new ArrayList<>();
        if(userName.equals("999") && password.equals("999")) {
            commands.add(controlCommand.setLoginInfo(userName));
            commands.add(switchScreenCommand.control());
        }else{
            if(StringUtils.isNotEmpty(userName)&&StringUtils.isNotEmpty(password)&&StringUtils.isNotEmpty(banci)&&StringUtils.isNotEmpty(jitai)){
                LoginDto loginDto=new LoginDto();
                loginDto.setPassword(password);
                loginDto.setPhone(userName);
                try {
                    Result res=userService.login(loginDto);
                    LgmnUserEntity userEntity = (LgmnUserEntity) res.getData();
                    LoginMap.get(ip).setLgmnUserEntity(userEntity);
                    if(res.getCode().equals("200")){
                        List<String> cleanCommands=controlCommand.cleanOrderInfo();
                        commands.addAll(cleanCommands);
                        commands.add(controlCommand.setPackNumColor("9F E6"));
                        commands.add(controlCommand.setLoginInfo(userName+ " "+ banci + " " +jitai));
                        commands.add(switchScreenCommand.control());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        sendCommand(ctx,commands);
    }

    /**
     * 登录界面 取消按钮 处理方法
     * @param ctx
     */
    public void event_login_cancel(ChannelHandlerContext ctx){
        List<String> commands = loginCommand.cleanAll();
        for (String command:commands ) {
            sendCommand(ctx,command);
        }
    }
    /**
     * ============================================================================
     * ====  end  ================ 登录界面按钮事件 ===============================
     * ============================================================================
     */

    /**
     * ============================================================================
     * ==== begin ================ 操作界面按钮事件 ===============================
     * ============================================================================
     */

    /**
     * 操作界面 注销按钮 处理方法
     * @param ctx
     */
    public void event_control_loginout(ChannelHandlerContext ctx){
        List<String> commands = controlCommand.cleanAll();
        for (String command:commands ) {
            sendCommand(ctx,command);
        }
        sendCommand(ctx,switchScreenCommand.login());
    }

    /**
     * 操作界面 打印按钮 处理方法 - 开始
     * @param ctx
     */
    public void event_control_print(ChannelHandlerContext ctx){
        getControlValues(ctx, "event_control_print");
        sendCommand(ctx,switchScreenCommand.printing());
    }

    /**
     * 操作界面 打印按钮 处理方法 - 完成
     * @param ctx
     */
    public void event_control_print_finish(ChannelHandlerContext ctx){
        String ip = getIPAddress(ctx);
        ControlEntity controlEntity = ControlMap.get(ip);

        /**
         * 完成数据获取后，清空会话事件
         */
        EventMap.add(ip,"");

        LoginEntity loginEntity = LoginMap.get(ip);

        /**
         * todo 打印按钮
         * 1. 生成标签数据
         * 2. 调用打印机打印
         * 3. 返回操作界面
         */


        if(loginEntity.getViewOrderInfoEntity()==null){
            sendCommand(ctx,switchScreenCommand.control());
            return;
        }

        int packnum=Integer.parseInt(controlEntity.getPackNum());
        int orderid=loginEntity.getViewOrderInfoEntity().getOrderId();

        String tipMsg="";
        int labelStatus=controlEntity.getStatus();
        if(labelStatus!=999) {
            switch (labelStatus) {
                case 0:
                case 1:
                    tipMsg="打印失败：标签已打印，请补打";
                    break;
                case 2:
                    tipMsg = "打印失败：标签已出库";
                    break;
                case 8:
                    tipMsg = "打印失败：标签已作废";
                    break;
                default:
                    tipMsg = "打印失败：未知错误";
                    break;
            }
            sendCommand(ctx,switchScreenCommand.control());
            sendCommand(ctx, controlCommand.setTipMsg(tipMsg));
            return;
        }else {
            if (packnum > controlEntity.getNextPackNum()) {
                tipMsg = "打印失败：最大流水号" + controlEntity.getNextPackNum();
                sendCommand(ctx,switchScreenCommand.control());
                sendCommand(ctx, controlCommand.setTipMsg(tipMsg));
                return;
            }
            try {
                int packNum = Integer.parseInt(controlEntity.getPackNum());
                int prodId = loginEntity.getViewOrderInfoEntity().getProdId();
                int modelId = loginEntity.getViewOrderInfoEntity().getModelId();
                BigDecimal weight = new BigDecimal(controlEntity.getSpecs());
                String handler = loginEntity.getLgmnUserEntity().getId();
                int quantity = Integer.parseInt(controlEntity.getQuantity());
                LabelRecordEntity labelRecordEntity = labelRecordService.createLabelRecord(packNum, orderid, prodId, modelId, handler, weight, quantity, ip);

                ViewLabelRecordDto viewLabelRecordDto = new ViewLabelRecordDto();
                viewLabelRecordDto.setPackId(packNum);
                viewLabelRecordDto.setLabelNum(labelRecordEntity.getLabelNum());
                ViewLabelRecordEntity viewLabelRecordEntity = viewLabelRecordApiService.findByDto(viewLabelRecordDto);
                if (viewLabelRecordDto != null) {
                    loginEntity.setLabelRecordEntity(viewLabelRecordEntity);
                    printing(loginEntity);

                    int packNo = labelRecordEntity.getPackId() + 1;
                    loginEntity.getViewOrderInfoEntity().setPackNum(packNo);

                    controlEntity.setCurrPackNum(controlEntity.getNextPackNum());
                    controlEntity.setNextPackNum(controlEntity.getNextPackNum() + 1);

                    List<String> commands=new ArrayList<>();
                    commands.add(controlCommand.clean(UmaCommand.CONTROLLER.TIPMSG));
                    commands.add(controlCommand.setPackNum(Integer.toString(packNo)));
                    commands.addAll(updateTotalNumCommands(controlEntity,ip));
                    sendCommand(ctx,commands);
                }
//            loginEntity.setLabelRecordEntity(viewLabelRecordApiService.getById(labelRecordEntity.getId()));
//            PrintUtil.print(path);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                sendCommand(ctx, switchScreenCommand.control());
            }
        }
    }

    /**
     * 操作界面 作废按钮 处理方法 - 开始
     * @param ctx
     */
    public void event_control_cancel(ChannelHandlerContext ctx) {
        getControlValues(ctx, "event_control_cancel");
    }

    /**
     * 操作界面 作废按钮 处理方法 - 完成
     * @param ctx
     */
    public void event_control_cancel_finish(ChannelHandlerContext ctx){
        String ip = getIPAddress(ctx);
        ControlEntity controlEntity = ControlMap.get(ip);

        /**
         * 完成数据获取后，清空会话事件
         */
        EventMap.add(ip,"");

        LoginEntity loginEntity = LoginMap.get(ip);
        ViewLabelRecordEntity labelRecordEntity = loginEntity.getLabelRecordEntity();

        /**
         * todo 作废按钮
         * 1. 更新数据
         *   1-1 没有标签数据不做操作
         *   1-2 判读标签数据状态
         */
        String tipMsg="";
        if(loginEntity.getViewOrderInfoEntity()==null || loginEntity.getLabelRecordEntity()==null){
            tipMsg="作废失败：找不到标签";
            List<String> commands = new ArrayList<>();
            commands.add(controlCommand.setTipMsg(tipMsg));
            sendCommand(ctx,commands);
            return;
        }
        int labelStatus=controlEntity.getStatus();
        if(labelStatus>1) {
            switch (labelStatus){
                case 2:
                    tipMsg = "作废失败：标签已出库";
                    break;
                case 8:
                    tipMsg = "作废失败：标签已作废";
                    break;
                default:
                    tipMsg = "作废失败：未知错误";
                    break;
            }
            sendCommand(ctx,controlCommand.setTipMsg(tipMsg));
            return;
        } else {
            labelRecordService.cancelLabelRecord(labelRecordEntity.getId(),Integer.parseInt(controlEntity.getPackNum()),loginEntity.getLgmnUserEntity().getId());
            //作废后恢复流水号
            sendCommand(ctx,controlCommand.clean(UmaCommand.CONTROLLER.TIPMSG));
            sendCommand(ctx,controlCommand.setPackNumColor("9F E6"));
            sendCommand(ctx,controlCommand.setPackNum(Integer.toString(controlEntity.getNextPackNum())));
        }
    }

    /**
     * 操作界面 补打按钮 处理方法 - 开始
     * @param ctx
     */
    public void event_control_reprint(ChannelHandlerContext ctx) {
        getControlValues(ctx, "event_control_reprint");
        sendCommand(ctx,switchScreenCommand.printing());
    }

    /**
     * 操作界面 补打按钮 处理方法 - 完成
     * @param ctx
     */
    public void event_control_reprint_finish(ChannelHandlerContext ctx){
        String ip = getIPAddress(ctx);
        ControlEntity controlEntity = ControlMap.get(ip);

        /**
         * 完成数据获取后，清空会话事件
         */
        EventMap.add(ip,"");

        LoginEntity loginEntity = LoginMap.get(ip);

        /**
         * todo 补打按钮
         * 1. 检查数据状态
         *   1-1 没有标签数据生成数据？
         *   1-2 出库、作废状态不作操作
         *   1-3 打印成功返回最后一个包号
         */
        String tipMsg="";
        if(loginEntity.getViewOrderInfoEntity()==null || loginEntity.getLabelRecordEntity()==null){
            tipMsg="补打失败：找不到标签";
            sendCommand(ctx,switchScreenCommand.control());
            sendCommand(ctx,controlCommand.setTipMsg(tipMsg));
            return;
        }
        int labelStatus=controlEntity.getStatus();
        if(labelStatus>1) {
            switch (labelStatus){
                case 2:
                    tipMsg = "补打失败：标签已出库";
                    break;
                case 8:
                    tipMsg = "补打失败：标签已作废";
                    break;
                default:
                    tipMsg = "补打失败：未知错误";
                    break;
            }
            sendCommand(ctx,switchScreenCommand.control());
            sendCommand(ctx,controlCommand.setTipMsg(tipMsg));
            return;
        } else {
            printing(loginEntity);
            List<String> commands=new ArrayList<>();
            //补打后恢复流水号
            commands.add(controlCommand.clean(UmaCommand.CONTROLLER.TIPMSG));
            commands.add(controlCommand.setPackNumColor("9F E6"));
            commands.add(controlCommand.setPackNum(Integer.toString(controlEntity.getNextPackNum())));
            commands.add(switchScreenCommand.control());
            sendCommand(ctx,commands);
        }

    }

    private void printing(LoginEntity loginEntity) {
        ViewOrderInfoEntity viewOrderInfoEntity = loginEntity.getViewOrderInfoEntity();
        ViewLabelRecordEntity labelRecordEntity = loginEntity.getLabelRecordEntity();

        LabelFormatDto dto = new LabelFormatDto();
        dto.setId(viewOrderInfoEntity.getLabelId());
        LabelFormatEntity labelFormatEntity = labelFormatService.getById(viewOrderInfoEntity.getLabelId());
        String jitai = loginEntity.getJitai().replace("机台","");

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日");
        PrintPojo printPojo=new PrintPojo();
        printPojo.setPackNum(labelRecordEntity.getPackId().toString());
        printPojo.setLabelNum(labelRecordEntity.getLabelNum());
        printPojo.setColor(viewOrderInfoEntity.getColor());
        printPojo.setJitai(jitai);
        printPojo.setPerPackQuantity(Integer.toString(labelRecordEntity.getQuantity()));
        printPojo.setSpecs(viewOrderInfoEntity.getSpecs());
        printPojo.setWidth(viewOrderInfoEntity.getWidth().toString().replace(".00",""));
        printPojo.setProdDate(sdf.format(labelRecordEntity.getProdTime()));
        printPojo.setProdUser(loginEntity.getUsername());
        printPojo.setModelName(labelRecordEntity.getModelName());

//        String tpath="C:/Users/Lonel/Desktop/test.xlsx";
        String path=ExcelUtils.exportLabelExcel(labelFormatEntity.getPath(),printPojo);
//        String path=ExcelUtils.exportLabelExcel(tpath,printPojo);
        try {
            PrintUtil.print(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> updateTotalNumCommands(ControlEntity controlEntity,String ip){
        List<String> commands=new ArrayList<>();
        ViewOrderInfoEntity viewOrderInfoEntity = getOrderInfoByOrderNo(controlEntity.getOrderNo(),ip);
        commands.add(controlCommand.setQuanTotal(viewOrderInfoEntity.getCumulativeQuantity()));
        commands.add(controlCommand.setWeightTotal(viewOrderInfoEntity.getCumulativeWeight()));
        return commands;
    }

    private void getControlValues(ChannelHandlerContext ctx, String control_reprint) {
        String ip = getIPAddress(ctx);
        ControlEntity controlEntity = ControlMap.get(ip);
        /**
         * 开始获取数据签，保存会话事件，并把完成状态设为false
         */
        EventMap.add(ip, control_reprint);
        controlEntity.setFinish(false);
        List<String> commands = controlCommand.getAllValue();
        for (String command : commands) {
            sendCommand(ctx, command);
        }
    }

    /**
     * ============================================================================
     * ====  end  ================ 操作界面按钮事件 ===============================
     * ============================================================================
     */

    /**
     * 发送指令
     * @param ctx
     * @param command
     */
    public void sendCommand(ChannelHandlerContext ctx,String command){
        ctx.channel().writeAndFlush(command);
    }

    public void sendCommand(ChannelHandlerContext ctx,List<String> commands){
        for (String command:commands) {
            ctx.channel().writeAndFlush(command);
        }
    }


    /**
     * 获取界面实体/指令
     * @param msgStr
     * @return
     */
    private String getScreen(String msgStr){
        String screnCommand=msgStr.substring(0,5);
        String value=umaCommand.findScreen(screnCommand);
        return value;
    }

    /**
     * 获取界面实体/指令
     * @param msgStr
     * @return
     */
    private String getControl(String screen,String msgStr){
        String screnCommand=msgStr.substring(6,11).toLowerCase();
        String value = "";
        if(StringUtils.isEmpty(screen)){
            logger.info("空了："+screen+":"+msgStr);
        }
        if(screen.equals("LoginEntity")) {
            value = umaCommand.findLogin(screnCommand);
        }else if(screen.equals("ControlEntity")){
            value = umaCommand.findControl(screnCommand);
        }
        return value;
    }

    /**
     * 获取输入框内容
     * @param msgStr
     * @return
     */
    private String getValue(String msgStr){
        if(msgStr.length()<16){
            return "";
        }
        logger.info(Integer.toString(msgStr.length()));
        String screnCommand=msgStr.substring(15);
        String value= CoderUtils.decoder(screnCommand.replaceAll(" ",""));
        return value;
    }

    /**
     * 保存会话连接
     * @param ctx
     * @return
     */
    public String SaveChannel(ChannelHandlerContext ctx) {
        String clientIP = getIPAddress(ctx);

        saveLogin(clientIP);
        saveControl(clientIP);
        saveEvent(clientIP);

        SocketChannel currentChannel = (SocketChannel)ctx.channel();

        if(ChannelMap.get(clientIP)==null){
            ChannelMap.add(clientIP, currentChannel);
        }else{
            SocketChannel oldChannel = (SocketChannel)ChannelMap.get(clientIP);
            if(!currentChannel.equals(oldChannel)){
                oldChannel.close();
                ChannelMap.add(clientIP,currentChannel);
            }
        }
        return clientIP;
    }

    /**
     * 保存登录界面实体对象
     * @param clientIP
     */
    public void saveLogin(String clientIP) {
        if(LoginMap.get(clientIP)==null){
            LoginMap.add(clientIP, new LoginEntity());
        }
    }

    /**
     * 保存操作界面实体对象
     * @param clientIP
     */
    public void saveControl(String clientIP) {
        if(ControlMap.get(clientIP)==null){
            ControlMap.add(clientIP, new ControlEntity());
        }
    }

    /**
     * 保存事件名称
     * @param clientIP
     */
    public void saveEvent(String clientIP) {
        if(EventMap.get(clientIP)==null){
            EventMap.add(clientIP, "");
        }
    }

    /**
     * 获取会话ip
     * @param ctx
     * @return
     */
    public String getIPAddress(ChannelHandlerContext ctx) {
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel()
                .remoteAddress();
        return insocket.getAddress().getHostAddress();
    }
}