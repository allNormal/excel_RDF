import {Type} from 'class-transformer';
import {CheckboxItem} from './checkbox-item';


export class WorkbookEndpoint {
  workbookName: string;
  @Type(() => Worksheet)
  worksheets: Worksheet[]
}

export class WorksheetEndpoint {
  worksheetName: string;
  active: boolean = true;
}

export class Worksheet extends WorksheetEndpoint{
  columns: Array<CheckboxItem> = [];
  rows:Array<CheckboxItem> = [];
  worksheetName: string;
  active: boolean;
}

export class WorksheetTable extends WorksheetEndpoint{
  rowHeader: string;
  columnHeader: string;
  table: Array<Table> = [];
  columnsRowsFrom: string;
}

export class Table {
  rows: Array<CheckboxItem> = [];
  columns: Array<CheckboxItem> = [];
  name: string;
}
