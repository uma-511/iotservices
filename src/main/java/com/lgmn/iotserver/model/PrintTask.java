package com.lgmn.iotserver.model;

import com.lgmn.utils.printer.Printer;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author: TJM
 * @Date: 2020/3/27 23:20
 */
@Data
@AllArgsConstructor
public class PrintTask {
    Printer printer;
}