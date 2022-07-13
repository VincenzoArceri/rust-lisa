fn main() {
    let mut _x = 42;
    let y = 2;
    let z = false;
    let w = &42;

    if _x > y { _x = 1; }
    if _x < y { _x = 1; }
    if _x == y { _x = 1; }
    if _x >= y { _x = 1; }
    if _x <= y { _x = 1; }
    if z && z { _x = 1; }
    if z || z { _x = 1; }
    if !z { _x = 1; }
    if &_x == w { _x = 1; }
    if &&_x == &&0 { _x = 1; }
    if *w == 0 { _x = 1; }
    if _x as i32 == 0{ _x = 1; }
    if _x ^ y == 0 { _x = 1; }
    if _x | y == 0 { _x = 1; }
    if _x & y == 0 { _x = 1; }
    if _x >> y == 0 { _x = 1; }
    if _x << y == 0 { _x = 1; }
    if _x + y == 0 { _x = 1; }
    if _x - y == 0 { _x = 1; }
    if _x * y == 0 { _x = 1; }
    if _x / y == 0 { _x = 1; }
    if _x % y == 0 { _x = 1; }
 }
