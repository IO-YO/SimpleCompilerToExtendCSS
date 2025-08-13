grammar ICSS;

// --- PARSER: ---
stylesheet
    : variableAssignment* stylerule*
    ;

// --- Variable Assignment:
// Var := 100px;
variableAssignment
    : variableReference ASSIGNMENT_OPERATOR expression SEMICOLON
    ;

// --- variableReference ---
variableReference: CAPITAL_IDENT;

// --- Style rules:
// #id { }
stylerule
    : selector body
    ;

// --- Selectors:
// #id, .class, div
selector
    : ID_IDENT      # idSelector
    | CLASS_IDENT   # classSelector
    | LOWER_IDENT   # tagSelector
    ;

body
    : OPEN_BRACE (variableAssignment | declaration | ifClause)* CLOSE_BRACE
    ;

// --- Declarations:
// width: 100px;
declaration
    : property COLON expression SEMICOLON
    ;

property
    : LOWER_IDENT
    ;

// --- Expressions ---
expression
    : expression (STAR) expression                      # multiplyOperation
    | expression (MINUS | PLUS) expression              # additiveOperation
    | literal                                           # literalExpression
    | variableReference                                 # variableReferenceExpression
    ;


// --- Literal expressions ---
literal
    : TRUE            # boolLiteral
    | FALSE           # boolLiteral
    | COLOR           # colorLiteral
    | PERCENTAGE      # percentageLiteral
    | PIXELSIZE       # pixelLiteral
    | SCALAR          # scalarLiteral
    ;

// --- if-else statement ---
ifClause
    : IF BOX_BRACKET_OPEN expression BOX_BRACKET_CLOSE body elseClause?
    ;

elseClause
    : ELSE body
    ;

// --- LEXER RULES -------------------------------------------------------------------------------

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';

// Literals:
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;

// Colors:
fragment HEXDIGIT: [0-9a-fA-F];
COLOR: '#' HEXDIGIT HEXDIGIT HEXDIGIT HEXDIGIT HEXDIGIT HEXDIGIT;

// Specific identifiers for id's and classes:
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

// General identifiers:
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

// Whitespace (skipped):
WS: [ \t\r\n]+ -> skip;

// Operators and punctuation:
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MINUS: '-';
STAR: '*';
SLASH: '/';
ASSIGNMENT_OPERATOR: ':=';
LPAREN: '(';
RPAREN: ')';
