grammar ICSS;

// --- PARSER: ---
stylesheet: variableAssignment* styleRule*;

// --- Variable Assignment and Reference ---
variableAssignment: variableReference ASSIGNMENT_OPERATOR expression SEMICOLON;
variableReference: CAPITAL_IDENT;

// --- Style rules ---
styleRule: selector body;
body: OPEN_BRACE (declaration | ifClause)* CLOSE_BRACE;

// --- Selectors
selector    : ID_IDENT      # idSelector
            | CLASS_IDENT   # classSelector
            | LOWER_IDENT   # tagSelector
            ;

// --- Declarations
declaration: property COLON expression SEMICOLON;
property: LOWER_IDENT;

// --- Expressions: variable references, literals, and operations
// TODO: Separate operations from expressions for better modularity to allow for more complex operations
expression  :   variableReference                   # variableReferenceExpression
            |   literal                             # literalExpression
            |   expression MUL expression   # multiplyOperation
            |   expression PLUS expression  # addOperation
            |   expression SUB expression   # subtractOperation
            ;

// This does not work; tests don't pass.
//            |   expression operation expression     # operationExpression
//            ;
//// --- Operations ---
//operation   :  MUL      # multiplyOperation
//            |  PLUS     # addOperation
//            |  SUB      # subtractOperation
//            ;

// --- Literal expressions ---
literal :   (TRUE | FALSE)  # boolLiteral
        |   COLOR           # colorLiteral
        |   PERCENTAGE      # percentageLiteral
        |   PIXELSIZE       # pixelLiteral
        |   SCALAR          # scalarLiteral
        ;

// --- if-else statement ---
ifClause: IF BOX_BRACKET_OPEN variableReference BOX_BRACKET_CLOSE body elseClause?;
elseClause: ELSE body;

// ---- LEXER: -------------------------------------------------------------------------------

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';

// Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;

// Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

// Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

// General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

// All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

// Special characters
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
SUB: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';
