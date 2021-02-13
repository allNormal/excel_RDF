import {Type} from 'class-transformer';
import {CheckboxItem} from './checkbox-item';


export class WorkbookEndpoint {
  workbookName: string;
  @Type(() => Worksheet)
  worksheets: Worksheet[]
}

export class Worksheet {
  columns: Array<string>;
  rows:Array<string>;
  worksheetName: string;
  active: boolean;
}

export class WorksheetTable {
  rowHeader: string;
  columnHeader: string;
  worksheetName: string;
  table: Array<Table> = [];
  active: boolean = true;
  columnsRowsFrom: string;
}

export class Table {
  rows: Array<CheckboxItem> = [];
  columns: Array<CheckboxItem> = [];
  name: string;
}
