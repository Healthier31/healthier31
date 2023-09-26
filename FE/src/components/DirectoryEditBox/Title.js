import { styled } from "styled-components";

import {
  DIRECTORY_EDIT_TITLE_TEXT,
  DIRECTORY_MODIFY_TITLE_TEXT,
} from "../../data/constants";

const Title = ({ EditPage }) => {
  return (
    <>
      <TitleSection>
        <TitleText>
          {EditPage ? DIRECTORY_EDIT_TITLE_TEXT : DIRECTORY_MODIFY_TITLE_TEXT}
        </TitleText>
      </TitleSection>
    </>
  );
};

export default Title;

const TitleSection = styled.section`
  wight: 100%;

  padding-left: 10px;

  display: flex;
  flex-direction: row;
  justify-content: start;
  align-items: start;
`;

const TitleText = styled.h1`
  font-size: 1.3rem;
  font-weight: bold;
`;
