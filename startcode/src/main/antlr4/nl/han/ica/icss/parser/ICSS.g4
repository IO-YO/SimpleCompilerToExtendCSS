grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';

//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;

//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

// Special characters
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';

//--- PARSER: ---
stylesheet: styleRule;

// --- Style rules ---
styleRule
: selector OPEN_BRACE declaration* CLOSE_BRACE; // | variableDeclaration

// --- Variable declaration and reference ---
// variableReference: CAPITAL_IDENT;
// variableDeclaration: variableReference ASSIGNMENT_OPERATOR propertyValue SEMICOLON;

// --- Selectors and declarations ---
selector
    : ID_IDENT
    | CLASS_IDENT
    | LOWER_IDENT;
declaration: property COLON propertyValue SEMICOLON;
property: LOWER_IDENT;
propertyValue
    : COLOR
    | ID_IDENT
    | CLASS_IDENT
    | PIXELSIZE
    | PERCENTAGE
    | SCALAR
    | TRUE
    | FALSE
    // | variableReference
    ;